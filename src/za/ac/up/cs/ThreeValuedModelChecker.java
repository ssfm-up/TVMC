package za.ac.up.cs;

import cnf.CNF;
import cnf.Formula;
import cnf.TseitinVisitor;
import cnf.Var;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cnf.CNF.*;

public class ThreeValuedModelChecker {

    private static final Formula FALSE = var(freshVar());
    private static final Formula TRUE = var(freshVar());
    static final Formula UNKNOWN = var(freshVar());
    static final int FALSE_VAL = 0;
    static final int TRUE_VAL = 1;
    static final int UNKNOWN_VAL = 2;
    final Map<String, Var> vars = new HashMap<>();
    // Used to lookup the number of the predicate
    final Map<String, Integer> predMap = new HashMap<>();
    final Map<Var, Integer> predUnknownMap = new HashMap<>();
    final Map<Formula, String> guardUnknownMap = new HashMap<>();

    private final Set<String> trueAssumptions = new HashSet<>();

    private final Set<String> falseAssumptions = new HashSet<>();

    private int maxBound;
    CFG cfgs;
    boolean checkFairness = false;
    Var[][] progress;
    private Properties config;
    TseitinVisitor tseitinVisitor;

    public ThreeValuedModelChecker(int maxBound) {
        this.maxBound = maxBound;
        this.tseitinVisitor = new TseitinVisitor();
    }

    public ThreeValuedModelChecker(CFG cfgs, int maxBound, Properties config) {
        this.cfgs = cfgs;
        this.maxBound = maxBound;
        this.predMap.putAll(cfgs.getPredicates());
        this.config = config;
        this.tseitinVisitor = new TseitinVisitor();
//        progress = initialiseProgressFlags();
    }

    public void setCfgs(CFG cfgs) {
        this.cfgs = cfgs;
        this.predMap.putAll(cfgs.getPredicates());
//        progress = initialiseProgressFlags();
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

    Var zVar(int bound) {
        return getNamedVar("z_" + bound);
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

    Var predUnknownVar(int process, int pred) {
        return getNamedVar("u_" + process + "_p_" + pred);
    }

    Var guardUnknownVar(int process, String guard) {
        return getNamedVar("u_" + process + "_g_" + guard);
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

    Formula constructAdditiveBaseCase(int maxBound) {
        return encodeTransitions(cfgs, maxBound - 1, maxBound);
    }

    Formula constructAdditiveStepCase(int maxBound, int numProcesses, int numOfLocs) {
        ArrayList<Formula> conjunctionFormulas = new ArrayList<>();

        for (int r = 0; r <= maxBound - 1; r++) {
            ArrayList<Formula> formulas = new ArrayList<>();

            for (int i = 0; i < numProcesses; i++) {
                for (int j = 0; j < numOfLocs; j++) {
                    Formula locR = encodeLocation(i, j, r, numOfLocs);
                    Formula locR2 = encodeLocation(i, j, maxBound, numOfLocs);
                    Formula term1 = and(locR, neg(locR2));
                    Formula term2 = and(neg(locR), locR2);
                    formulas.add(or(term1, term2));
                }
            }
            conjunctionFormulas.add(or(formulas));
        }

        return and(encodeTransitions(cfgs, maxBound - 1, maxBound), and(conjunctionFormulas));
    }

    Formula constructStepFormula(Formula ltlPropertyEncoding, int numProcesses, int numLocs, boolean stepWithInit) {
        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);
        Formula loopFreeK = loopFree(maxBound, numProcesses, numLocs);

        Formula formula;

        if (stepWithInit) {
            // Add encoding for initial state so that no predicates are unknown
            List<Formula> formulas = new ArrayList<>();
            for (int p = 0; p < cfgs.getNumberOfPredicates(); p++) {
                final Formula pKnown = neg(var(predVar(p, 0, false)));
                formulas.add(pKnown);
            }
            final Formula init = and(formulas);
            formula = and(init, transitionEncoding, ltlPropertyEncoding, loopFreeK, TRUE, neg(FALSE));
        } else {
            formula = and(transitionEncoding, ltlPropertyEncoding, loopFreeK, TRUE, neg(FALSE));
        }

        return formula;
    }

    Formula loopFree(int k, int numProcesses, int numOfLocs) {
        ArrayList<Formula> conjunctionFormulas = new ArrayList<>();

        for (int r = 0; r <= k - 1; r++) {
            for (int r2 = r + 1; r2 <= k; r2++) {
                ArrayList<Formula> formulas = new ArrayList<>();

                for (int i = 0; i < numProcesses; i++) {
                    for (int j = 0; j < numOfLocs; j++) {
                        Formula locR = encodeLocation(i, j, r, numOfLocs);
                        Formula locR2 = encodeLocation(i, j, r2, numOfLocs);
                        Formula term1 = and(locR, neg(locR2));
                        Formula term2 = and(neg(locR), locR2);
                        formulas.add(or(term1, term2));
                    }
                }
                conjunctionFormulas.add(or(formulas));
            }
        }
        return and(conjunctionFormulas);
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
        return checkSatisfiability(formula, SolverFactory.newDefault(), null);
    }


    /**
     * Check is a satisfying assignment for a formula can be found
     *
     * @param formula     The formula to be checked
     * @param solver      A solver to be used for the satisfiability check
     * @param constraints The constraints that should not be learned by the solver
     * @return Is the formula satisfiable
     */
    boolean checkSatisfiability(Formula formula, ISolver solver, IVecInt constraints) {
//        tseitinVisitor = new TseitinVisitor();
        Integer x = formula.accept(tseitinVisitor);
        Formula cnfFormula = tseitinVisitor.getResultFormula(x);
//        Formula cnfFormula = cnf(formula, tseitinVisitor);

        try {
            Set<Var> trueVars = CNF.satisfiable(cnfFormula, solver, constraints, tseitinVisitor, x);
            if (trueVars != null) {
                System.out.println("SATISFIABLE");
                final Path path = generateExecutionPath(trueVars, true);
                System.out.println();
                System.out.println(path);
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

    boolean checkSatisfiability(ISolver solver, IVecInt constraints, boolean printTrueVars) {
        try {
            if (solver.isSatisfiable(constraints)) {
                int[] model = solver.model();
                Set<Var> trueVars = new HashSet<>();
                for (Integer y : model) {
                    if (y > 0) {
                        trueVars.add(new Var(y));
                    }
                }

                System.out.println("SATISFIABLE");
                final Path path = generateExecutionPath(trueVars, printTrueVars);
                if (printTrueVars) {
                    System.out.println();
                    System.out.println(path);
                }


                String[] split = path.toString().split("\n");
                int i = 0;
                while (i < split.length) {
                    String stepStr = split[i];
                    final int indexOfUnknownTrans = stepStr.indexOf("choice(false, false)");
                    if (indexOfUnknownTrans != -1) {

                        break;
                    }

                    i++;
                }

                if (i != split.length) {
                    System.out.println("First unknown transition is from state " + i + ": " + path.steps.get(i));

                    for (int j = 0; j <= i; j++) {
                        final Step currentStep = path.steps.get(j);
                        trueAssumptions.addAll(currentStep.locations);
                        trueAssumptions.addAll(currentStep.predicates);

                        final Stream<String> stringStream = vars.keySet().stream().filter(s -> {
                            if (s.startsWith("l_")) {
                                final String boundStr = s.substring(s.lastIndexOf('_') + 1, s.length());
                                final int bound = Integer.parseInt(boundStr);
                                if (bound <= currentStep.getBound()) return true;
                            }

                            if (s.startsWith("p_")) {
                                String boundStr = s.substring(2).substring(s.indexOf('_') + 1);
                                boundStr = boundStr.substring(0, boundStr.lastIndexOf('_'));
                                final int bound = Integer.parseInt(boundStr);
                                if (bound <= currentStep.getBound()) return true;
                            }

                            return false;
                        });

                        final Set<String> allVars = stringStream.collect(Collectors.toSet());
                        allVars.removeAll(trueAssumptions);
                        falseAssumptions.addAll(allVars);
                    }

                    System.out.println("trueAssumptions = " + trueAssumptions);
                    System.out.println("falseAssumptions = " + falseAssumptions);
                }


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

    private Path generateExecutionPath(Set<Var> trueVars, boolean printTrueVars) {
        if (printTrueVars)
            System.out.println("True Variables:");
        Path executionPath = new Path(cfgs, maxBound);
        for (String key : new TreeSet<>(vars.keySet())) {
            if (trueVars.contains(vars.get(key))) {
                if (key.startsWith("p_")) {
                    executionPath.addPredicate(key);
                } else if (key.startsWith("l_")) {
                    executionPath.addLocation(key);
                } else if (key.startsWith("u_")) {
                    // TODO: Add unknown to list for use in refinement
                } else if (key.startsWith("z_")) {

                } else {
                    executionPath.addProgressStep(key);
                }
                if (printTrueVars)
                    System.out.println(key);
            }
        }

        // TODO: Use list of true unknowns here for refinement
        return executionPath;
    }


    public void clearAssumptions() {
        trueAssumptions.clear();
        falseAssumptions.clear();
    }

    /**
     * Print a table showing the mapping from variables names to aliases in solvers' naming conventions
     */
    void printVars() {
        System.out.println("Variable Mappings:");
        System.out.println("=====================================");
        System.out.println(String.format("| %1$-15s", FALSE) + " | " + String.format("%1$-15s |", "FALSE"));
        System.out.println(String.format("| %1$-15s", TRUE) + " | " + String.format("%1$-15s |", "TRUE"));
        System.out.println(String.format("| %1$-15s", UNKNOWN) + " | " + String.format("%1$-15s |", "UNKNOWN"));
        for (Map.Entry<String, Var> e : vars.entrySet()) {
            System.out.println(String.format("| %1$-15s", e.getValue()) + " | " + String.format("%1$-15s |", e.getKey()));
        }
        System.out.println("=====================================");
    }

    void printFormula(Formula formula) {
        final String[] formulaString = {formula.toString()};

        vars.forEach((s, var) -> {
            formulaString[0] = formulaString[0].replaceAll("x" + var.number, s);
        });

        formulaString[0] = formulaString[0].replaceAll(FALSE.toString(), "FALSE");
        formulaString[0] = formulaString[0].replaceAll(TRUE.toString(), "TRUE");
        formulaString[0] = formulaString[0].replaceAll(UNKNOWN.toString(), "UNKNOWN");

        System.out.println(formulaString[0]);
    }

    /**
     * See Definition 10
     *
     * @param cfgs
     * @param bound
     * @return
     */
    private Formula encodeTransitions(CFG cfgs, int bound) {
        return encodeTransitions(cfgs, 0, bound);
    }

    private Formula encodeTransitions(CFG cfgs, int startBound, int maxBound) {
        if (startBound < 0) startBound = 0;
        List<Formula> formulas = new ArrayList<>();
        for (int i = startBound; i < maxBound; i++) {

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
        LogicParser parser = new LogicParser(this, vars, TRUE, FALSE, UNKNOWN, bound, process);
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

    public void setMaxBound(int maxBound) {
        this.maxBound = maxBound;
    }

    public List<Integer> getTrueAssumptions() {
        return trueAssumptions.stream().map(s -> getNamedVar(s).number).collect(Collectors.toList());
    }

    public List<Integer> getFalseAssumptions() {
        return falseAssumptions.stream().map(s -> -getNamedVar(s).number).collect(Collectors.toList());
    }
}