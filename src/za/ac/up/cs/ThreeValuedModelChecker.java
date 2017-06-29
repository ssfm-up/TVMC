package za.ac.up.cs;

import cnf.CNF;
import cnf.Formula;
import cnf.Var;
import org.sat4j.specs.TimeoutException;

import java.util.*;

import static cnf.CNF.*;

public class ThreeValuedModelChecker {

    static final Formula FALSE = var(freshVar());
    static final Formula TRUE = var(freshVar());
    static final Formula UNKNOWN = var(freshVar());
    static final int FALSE_VAL = 0;
    static final int TRUE_VAL = 1;
    static final int UNKNOWN_VAL = 2;
    final CFG cfgs;
    final Map<String, Var> vars = new HashMap<>();
    // Used to lookup the number of the predicate
    final Map<String, Integer> predMap = new HashMap<>();
    final int maxBound;
    boolean checkFairness = true;
    Var[][] progress;
    Properties config;

    public ThreeValuedModelChecker(CFG cfgs, int maxBound, Properties config) {
        this.cfgs = cfgs;
        this.maxBound = maxBound;
        this.predMap.putAll(cfgs.getPredicates());
        this.config = config;
        progress = initialiseProgressFlags();
    }

    /**
     * See Definition 7
     *
     * @param process
     * @param loc
     * @param bound
     * @param numberOfLocs
     * @return
     */
    Formula encodeLocation(int process, int loc, int bound, int numberOfLocs) {
        int numOfBinaryDigits = (int) Math.ceil(Math.log(numberOfLocs) / Math.log(2.0));

        List<Formula> formulas = new LinkedList<>();

        // Convert loc to binary and then AND each binary digit together
        int oldLoc;
        for (int i = numOfBinaryDigits; i > 0; i--) {
            oldLoc = loc;
            loc /= 2;
            int remainder = oldLoc - loc * 2;
            if (remainder == 0) {
                formulas.add(neg(var(locVar(process, i - 1, bound))));
            } else {
                assert remainder == 1;
                formulas.add(var(locVar(process, i - 1, bound)));
            }
        }

        return and(formulas);
    }

    private Var locVar(int process, int loc, int bound) {
        return getNamedVar("l_" + process + "_" + loc + "_" + bound);
    }

    /**
     * See definition 8
     */
    Formula encodePredicate(int pred, int bound, int value) {
        Formula f1;
        Formula f2;
        switch (value) {
            case TRUE_VAL:
                // True
                f1 = neg(var(predVar(pred, bound, false)));
                f2 = var(predVar(pred, bound, true));
                return and(f1, f2);
            case FALSE_VAL:
                // False
                f1 = neg(var(predVar(pred, bound, false)));
                f2 = neg(var(predVar(pred, bound, true)));
                return and(f1, f2);
            case UNKNOWN_VAL:
                // Unknown
                return var(predVar(pred, bound, false));
            default:
                System.out.println("Invalid value");
                return null;
        }
    }

    Var predVar(int pred, int bound, boolean known) {
        return getNamedVar("p_" + pred + "_" + bound + "_" + (known ? "b" : "u"));
    }

    /**
     * Method for calculating F(G(Psi)) where
     * F is Finally
     * G is Globally
     * Psi represents the conjunction of encodings of a predicate
     * See Definition 11 for further details
     *
     * @return Returns encoding formula
     */
    private Formula constructLtlEncodingLiveness(int process, int CRITICAL) {
        // Get a reference to the initial process and store its state count
        Process cfg = cfgs.getProcess(process);
        int processStateCount = cfg.getStateCount();

        // Define a list of formulas to be used when calculating Finally
        List<Formula> formulas = new ArrayList<>();

        for (int r = 0; r <= maxBound; r++) {
            Formula transitionFormula = encodeTransition(cfg, process, maxBound, r);
            // Add the result of Globally to the list of formulas
            // Trans(b,r) /\ ( \/ Encodings )
            formulas.add(and(transitionFormula, simpleLiveness(process, processStateCount, CRITICAL, r), ufair(r)));
        }
        // Return the result of Finally
        return or(formulas);
    }

    private Formula simpleLiveness(int process, int stateCount, int CRITICAL, int r) {
        // Define a list of formulas to be used when calculating Globally
        List<Formula> globally = new ArrayList<>();
        for (int k = 0; k <= maxBound; k++) {
            List<Formula> encodings = new ArrayList<>();
            for (int kprime = Math.min(k, r); kprime <= maxBound; kprime++) {
                encodings.add(encodeLocation(process, CRITICAL, kprime, stateCount));
            }
            // Add the conjunction of all encodings
            // Encodings := /\ encodeLocation
            globally.add(and(encodings));
        }
        return or(globally);
    }

    /**
     * Calculates and returns Globally Finally progress[i][k] where i is the process number and k is the current position
     * of the b-loop
     *
     * @return
     */
    private Formula ufair(int r) {
        List<Formula> forAllProcesses = new ArrayList<>();
        for (int i = 0; i < cfgs.getNumberOfProcesses(); ++i) {
            // Define a list of formulas to be used when calculating Finally
            List<Formula> finallyFormulas = new ArrayList<>();
            // Finally within the loop
            for (int k = r; k <= maxBound; k++) {
                finallyFormulas.add(var(progress[i][k]));
            }
            forAllProcesses.add(or(finallyFormulas));
        }
        return and(forAllProcesses);
    }

    Formula constructFormula(Formula ltlPropertyEncoding) {
        ArrayList<Formula> initialProgressValues = new ArrayList<>();
        ArrayList<Formula> initialState = new ArrayList<>();

        List<Process> processes = cfgs.getProcesses();
        for (int i = 0; i < processes.size(); ++i) {
            initialState.add(encodeLocation(i, 0, 0, processes.get(i).getStateCount()));
            if (checkFairness) initialProgressValues.add(neg(var(progress[i][0])));
        }

        predMap.forEach((predStr, pred) -> {
            // TODO: Extend to support more types of predicates, not just semaphores
            initialState.add(encodePredicate(pred, 0, predStr.contains("-1") ? TRUE_VAL : FALSE_VAL));
        });

        if (checkFairness) initialState.add(and(initialProgressValues));

        Formula init = and(initialState);
        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        if (checkFairness) {
            // TODO: Pass process ID and critical line in from Main, possibly through config object
            init = and(init, constructLtlEncodingLiveness(0, 1));
        }

        if (ltlPropertyEncoding == null) return and(init, transitionEncoding, TRUE, neg(FALSE));

        return and(init, transitionEncoding, ltlPropertyEncoding, TRUE, neg(FALSE));
    }

    Formula getNotUnknownFormula(Formula formula) {
        return and(formula, neg(UNKNOWN));
    }

    Formula getUnknownFormula(Formula formula) {
        return and(formula, UNKNOWN);
    }

    /**
     * Check is a satisfying assignment for a formula can be found
     *
     * @param formula The formula to be checked
     */
    boolean checkSatisfiability(Formula formula) {
        Formula cnfFormula = cnf(formula);
        try {
            Set<Var> trueVars = CNF.satisfiable(cnfFormula);
            if (trueVars != null) {
                System.out.println("SATISFIABLE");
                System.out.println("True Variables:");
                Path executionPath = new Path(cfgs, maxBound);
                for (String key : new TreeSet<>(vars.keySet())) {
                    if (trueVars.contains(vars.get(key))) {
                        if (key.startsWith("p_")) {
                            executionPath.addPredicate(key);
                        } else if (key.startsWith("l_")) {
                            executionPath.addLocation(key);
                        } else {
                            executionPath.addProgressStep(key);
                        }
                        System.out.println(key);
                    }
                }
                System.out.println();
                System.out.println(executionPath);
                return true;
            } else {
                System.out.println("NOT SATISFIABLE");
                return false;
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Print a table showing the mapping from variables names to aliases in solvers' naming conventions
     */
    void printVars() {
        System.out.println("Variable Mappings:");
        System.out.println("=====================================");
        for (Map.Entry<String, Var> e : vars.entrySet()) {
            System.out.println(String.format("| %1$-15s", e.getValue()) + " | " + String.format("%1$-15s |", e.getKey()));
        }
        System.out.println("=====================================");
    }

    /**
     * See Definition 10
     *
     * @param cfgs
     * @param bound
     * @return
     */
    private Formula encodeTransitions(CFG cfgs, int bound) {
        List<Formula> formulas = new ArrayList<>();
        for (int i = 0; i < bound; i++) {

            List<Formula> processTransitions = new ArrayList<>();
            List<Process> processes = cfgs.getProcesses();
            for (int proc = 0; proc < processes.size(); proc++) {
                Formula formula = encodeTransition(processes.get(proc), proc, i, i + 1);
                processTransitions.add(formula);
            }

            formulas.add(or(processTransitions));
        }

        return and(formulas);
    }

    /**
     * Encodes the transitions of a single CFG to a certain bound
     * See definition 3 (Encoding of Transitions)
     *
     * @param cfg     The Control Flow Graph representing the application
     * @param process The unique identifier of the process, ie. the index in the CFG
     * @param bound   The bound until which the transitions should be encoded
     * @param r       TODO: Ask Nils for a good explanation of this parameter
     * @return A Formula representing the encoded transitions
     */
    private Formula encodeTransition(Process cfg, int process, int bound, int r) {
        LogicParser parser = new LogicParser(predMap, vars, TRUE, FALSE, UNKNOWN, bound);
        return cfg.getEncoding(this, process, bound, r, parser);
    }

    private Var getNamedVar(String s) {
        vars.putIfAbsent(s, freshVar());
        return vars.get(s);
    }

    private Var[][] initialiseProgressFlags() {
        Var[][] progressFlags = new Var[cfgs.getNumberOfProcesses()][maxBound + 1];
        for (int i = 0; i < cfgs.getNumberOfProcesses(); ++i) {
            for (int j = 0; j < maxBound + 1; ++j)
                progressFlags[i][j] = getNamedVar("progress_" + i + "_" + j);
        }
        return progressFlags;
    }
}