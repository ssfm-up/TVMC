package za.ac.up.cs;

import cnf.CNF;
import cnf.Formula;
import cnf.Var;
import org.sat4j.specs.TimeoutException;

import java.util.*;

import static cnf.CNF.*;

public class ThreeValuedModelChecker {

    private static final Formula FALSE = var(freshVar());
    private static final Formula TRUE = var(freshVar());
    private static final Formula UNKNOWN = var(freshVar());
    private static final int FALSE_VAL = 0;
    private static final int TRUE_VAL = 1;
    private static final int UNKNOWN_VAL = 2;
    private final CFG cfgs;
    private final Map<String, Var> vars = new HashMap<>();
    // Used to lookup the number of the predicate
    private final Map<String, Integer> predMap = new HashMap<>();
    private final int maxBound;
    //private final int numberOfPreds;
    // TODO: use a Properties object to store configuration instead
    private boolean checkFairness = true;
    //private final int numberOfProcesses;
    private Var[][] progress;

    public ThreeValuedModelChecker(CFG cfgs, int maxBound) {
        this.cfgs = cfgs;
        this.maxBound = maxBound;
        this.predMap.putAll(cfgs.getPredicates());
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
    private Formula encLoc(int process, int loc, int bound, int numberOfLocs) {
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
    private Formula encPred(int pred, int bound, int value) {
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

    private Var predVar(int pred, int bound, boolean known) {
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
    private Formula constructLtlEncodingLiveness() {
        // Get a reference to the initial process and store its state count
        Process cfg = cfgs.getProcess(0);
        int stateCountPc_0 = cfg.getStateCount();

        // Define a list of formulas to be used when calculating Finally
        List<Formula> formulas = new ArrayList<>();
        // The number of the line of code to be used as the CRITICAL section
        int CRITICAL = 3;

        for (int r = 0; r < maxBound; r++) {
            Formula transitionFormula = encodeTransition(cfg, 0, maxBound, r);
            // Add the result of Globally to the list of formulas
            // Trans(b,r) /\ ( \/ Encodings )
            formulas.add(and(transitionFormula, simpleLiveness(0, stateCountPc_0, CRITICAL, r), ufair(r)));
        }
        // Return the result of Finally
        return or(formulas);
    }

    private Formula simpleLiveness(int process, int stateCount, int CRITICAL, int r) {
        // Define a list of formulas to be used when calculating Globally
        List<Formula> globally = new ArrayList<>();
        for (int k = 0; k < maxBound; k++) {
            List<Formula> encodings = new ArrayList<>();
            for (int kprime = Math.min(k, r); kprime < maxBound; kprime++) {
                encodings.add(encLoc(process, CRITICAL, k, stateCount));
            }
            // Add the conjunction of all encodings
            // Encodings := /\ encLoc
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
            for (int k = r; k < maxBound; k++) {
                finallyFormulas.add(var(progress[i][k]));
            }
            forAllProcesses.add(or(finallyFormulas));
        }
        return and(forAllProcesses);
    }

    public Formula constructFormula(Formula ltlPropertyEncoding) {
        ArrayList<Formula> initialProgressValues = new ArrayList<>();
        ArrayList<Formula> initialState = new ArrayList<>();

        List<Process> processes = cfgs.getProcesses();
        for (int i = 0; i < processes.size(); ++i) {
            initialState.add(encLoc(i, 0, 0, processes.get(i).getStateCount()));
            if (checkFairness) initialProgressValues.add(neg(var(progress[i][0])));
        }

        predMap.forEach((predStr, pred) -> {
            initialState.add(encPred(pred, 0, predStr.contains("-1") ? TRUE_VAL : FALSE_VAL));
        });

        if (checkFairness) initialState.add(and(initialProgressValues));

        Formula init = and(initialState);
        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        if (checkFairness) {
            init = and(init, constructLtlEncodingLiveness());
        }


        if (ltlPropertyEncoding == null) return and(init, transitionEncoding, TRUE, neg(FALSE));

        return and(init, transitionEncoding, ltlPropertyEncoding, TRUE, neg(FALSE));
    }

    public Formula getNotUnknownFormula(Formula formula) {
        return and(formula, neg(UNKNOWN));
    }

    public Formula getUnknownFormula(Formula formula) {
        return and(formula, UNKNOWN);
    }

    /**
     * Check the satisfiability of a formula using Sat4j
     *
     * @param formula
     */
    public void checkSatisfiability(Formula formula) {
        Formula cnfFormula = cnf(formula);
        try {
            Set<Var> trueVars = CNF.satisfiable(cnfFormula);
            if (trueVars != null) {
                System.out.println("SATISFIABLE");
                System.out.println("True Variables:");
                for (String key : new TreeSet<>(vars.keySet())) {
                    if (trueVars.contains(vars.get(key))) {
                        System.out.println(key);
                    }
                }
            } else {
                System.out.println("NOT SATISFIABLE");
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void printVars() {
        System.out.println("Variable Mappings:");
        System.out.println("=====================================");
        for (Map.Entry<String, Var> e : vars.entrySet()) {
            System.out.println(String.format("| %1$-15s", e.getValue()) + " | " + String.format("%1$-15s |", e.getKey()));
        }
        System.out.println("=====================================");
    }

    // TODO: Make difference between these two methods clear - Possibly rename one?

    /**
     * @param cfgs
     * @param bound
     * @return
     */
    private Formula encodeTransition(CFG cfgs, int bound) {
        List<Formula> formulas = new ArrayList<>();
        List<Process> processes = cfgs.getProcesses();
        for (int proc = 0; proc < processes.size(); proc++) {
            Formula formula = encodeTransition(processes.get(proc), proc, bound);
            formulas.add(formula);
        }
        return or(formulas);
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

        for (int i = 0; i < bound - 1; i++) {
            formulas.add(encodeTransition(cfgs, i));
        }

        return and(formulas);
    }

    /**
     * A special case of encodeTransition where r = bound + 1
     *
     * @param cfg     The Control Flow Graph representing the application
     * @param process The unique identifier of the process, ie. the index in the CFG
     * @param bound   The bound until which the transitions should be encoded
     * @return A Formula representing the encoded transitions
     */
    private Formula encodeTransition(Process cfg, int process, int bound) {
        return encodeTransition(cfg, process, bound, bound + 1);
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
        int stateCount = cfg.getStateCount();
        List<Formula> formulas = new LinkedList<>();
        LogicParser parser = new LogicParser(predMap, vars, TRUE, FALSE, UNKNOWN, bound);

        for (State state : cfg.getStates()) {

            List<Transition> transitions = state.getTransitions();
            for (Transition transition : transitions) {
                List<Formula> currentTransEncoding = new LinkedList<>();

                int source = transition.getSource();
                int destination = transition.getDestination();
                Formula locEncoding = and(encLoc(process, source, bound, stateCount), encLoc(process, destination, r, stateCount));
                currentTransEncoding.add(locEncoding);

                if (checkFairness) {
                    // /\ progress[i] [k+1]
                    currentTransEncoding.add(var(progress[process][r]));
                }

                for (int i = 0; i < cfgs.getNumberOfProcesses(); i++) {
                    if (i != process) {
                        // TODO Pass in THAT process's stateCount, not this one's
                        Formula idlingEncoding = idleTransitionEncoding(i, cfgs.getProcess(i).getStateCount(), bound);
                        currentTransEncoding.add(idlingEncoding);

                        if (checkFairness) {
                            // /\ not progress[j][k+1]
                            currentTransEncoding.add(neg(var(progress[i][r])));
                        }
                    }
                }

                if (transition.hasGuard()) {
                    String s = transition.getGuard();
                    //System.out.println("Guard: " + s);
                    ParserHelper parserHelper = new ParserHelper(parser, s, predMap).invoke();
                    Formula a = parserHelper.getA();
                    Formula b = parserHelper.getB();

                    String s1 = s.split(",")[1];
                    String bRaw = s1.substring(0, s1.length() - 1).trim();
                    if (bRaw.startsWith("(")) {
                        bRaw = bRaw.substring(1, bRaw.length() - 1);
                    }
                    String str = "not(" + bRaw + ")";
                    String modifiedB = parserHelper.cleanExpression(str);
                    Formula notB = parserHelper.getParser().LOGIC_PARSER.parse(modifiedB);
                    // choice(a, b) = (a or not b) and (a or b or unknown)
                    Formula choiceEncoding = and(or(a, notB), or(a, b, UNKNOWN));
                    currentTransEncoding.add(choiceEncoding);
                }

                List<Integer> assignedPredicates = new ArrayList<>();
                for (Assignment assignment : transition.getAssignments()) {
                    ParserHelper parserHelper = new ParserHelper(parser, assignment.getRHS(), predMap).invoke();
                    Formula a = parserHelper.getA();
                    Formula b = parserHelper.getB();

                    Integer pred = assignment.getPredicate();
                    assignedPredicates.add(pred);

                    Formula f1 = and(a, encPred(pred, r, TRUE_VAL));
                    Formula f2 = and(b, encPred(pred, r, FALSE_VAL));
                    Formula f3 = and(and(neg(a), neg(b)), encPred(pred, r, UNKNOWN_VAL));

                    Formula or = or(f1, f2, f3);
                    currentTransEncoding.add(or);
                }
                currentTransEncoding.addAll(idlePredicateEncoding(assignedPredicates, bound, r));

                formulas.add(and(currentTransEncoding));
            }
        }
        return or(formulas);
    }

    private Formula idleTransitionEncoding(int process, int stateCount, int bound) {
        List<Formula> formulas = new ArrayList<>();

        for (int i = 0; i < stateCount; i++) {
            Formula a = encLoc(process, i, bound, stateCount);
            Formula b = encLoc(process, i, bound + 1, stateCount);
            formulas.add(iff(a, b));
        }

        return and(formulas);
    }

    private List<Formula> idlePredicateEncoding(List<Integer> excludedPreds, int bound, int r) {
        List<Formula> encoding = new ArrayList<>();
        predMap.forEach((predStr, pred) -> {
            if (!excludedPreds.contains(pred)) {
                //System.out.println("Adding idle encoding for: " + pred);
                Formula pUK = var(predVar(pred, bound, false));
                Formula pUKp1 = var(predVar(pred, r, false));
                Formula f1 = and(or(neg(pUK), pUKp1), or(pUK, neg(pUKp1)));

                Formula pTK = var(predVar(pred, bound, true));
                Formula pTKp1 = var(predVar(pred, r, true));
                Formula f2 = and(or(neg(pTK), pTKp1), or(pTK, neg(pTKp1)));
                encoding.add(and(f1, f2));
            }
        });
        return encoding;
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