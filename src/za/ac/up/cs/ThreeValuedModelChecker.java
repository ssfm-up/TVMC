package za.ac.up.cs;

import cnf.CNF;
import cnf.Formula;
import cnf.Var;
import de.upb.agw.jni.*;
import org.jetbrains.annotations.NotNull;
import org.sat4j.specs.TimeoutException;

import java.util.*;

import static cnf.CNF.*;

/**
 * Created by Matthias on 2016/04/15.
 * Project: Spotlight
 */
public class ThreeValuedModelChecker {

    private static final Formula FALSE = var(freshVar());
    private static final Formula TRUE = var(freshVar());
    private static final Formula UNKNOWN = var(freshVar());
    private static final int FALSE_VAL = 0;
    private static final int TRUE_VAL = 1;
    private static final int UNKNOWN_VAL = 2;
    private final EnumeratorOfCFGraph cfgs;
    // TODO change to private
    public final Map<String, Var> vars = new HashMap<>();
    // Used to lookup the number of the predicate
    private final Map<String, Integer> predMap = new HashMap<>();
    private final int maxBound;
    private final int numberOfPreds;
	// TODO: use a Properties object to store configuration instead
    private boolean checkFairness = false;
    private final int numberOfProcesses;
    private Var[][] progress;

    public ThreeValuedModelChecker(EnumeratorOfExpression predicates, EnumeratorOfCFGraph cfgs, int maxBound) {
        this.cfgs = cfgs;

//        this.numberOfLocs = 4;
        this.maxBound = maxBound;
//        states = new ArrayList<>();
        numberOfPreds = predicates.getNumberofElements();
//        numberOfPreds = 2;
        int i = 0;
        predicates.reset();
        while (predicates.hasNext()) {
            Expression next = predicates.getNext();
            predMap.put(next.__toString(), i);
//            System.out.println("p" + i + " : " + next.__toString());
            i++;
        }
        predicates.reset();
        cfgs.reset();
        numberOfProcesses = cfgs.getNumberofElements();
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

    public Formula constructLtlEncoding() {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();

        // Finally ( pc_0 = Critical AND pc_1 = Critical) ---> Should be false
        List<Formula> formulas = new ArrayList<>();
        for (int bound = 0; bound < maxBound; bound++) {
            int loc = 3;
            Formula bothProcessesInCritical = and(encLoc(0, loc, bound, stateCountPc_0), encLoc(1, loc, bound, stateCountPc_1));
            formulas.add(bothProcessesInCritical);
        }
        return or(formulas);
    }

    public Formula constructLtlEncoding2() {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();

        // Finally ( pc_0 = Critical) AND Finally (pc_1 = Critical) ---> Should be true. bound 5

        int critLoc = 3;
        List<Formula> formulas = new ArrayList<>();
        for (int bound = 0; bound < maxBound; bound++) {
            formulas.add(encLoc(0, critLoc, bound, stateCountPc_0));
        }
        Formula leftFormula = or(formulas);

        formulas = new ArrayList<>();
        for (int bound = 0; bound < maxBound; bound++) {
            formulas.add(encLoc(1, critLoc, bound, stateCountPc_1));
        }
        Formula rightFormula = or(formulas);

        return and(leftFormula, rightFormula);
    }

    // (pc_0=0) /\ (EF((pc_1=WAIT1) /\ (pc_2=WAIT2))) ---> Should be True
    public Formula constructLtlEncodingMutex2() {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();
        int stateCountPc_2 = cfgs.getNext().getStateCount();

        List<Formula> formulas = new ArrayList<>();
        for (int bound = 0; bound < maxBound; bound++) {
            int WAIT1 = 7;
            int WAIT2 = 4;
            Formula bothProcessesInCritical = and(encLoc(1, WAIT1, bound, stateCountPc_1), encLoc(2, WAIT2, bound, stateCountPc_2));
//            Formula processZeroAtLocZero = encLoc(0, 0, bound, stateCountPc_0);
//            formulas.add(and(processZeroAtLocZero, bothProcessesInCritical));
            formulas.add(bothProcessesInCritical);
        }

        return or(formulas);
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
    public Formula constructLtlEncodingLiveness() {
        cfgs.reset();

        // Get a reference to the initial process and store its state count
        CFGraph cfg = cfgs.getNext();
        // TODO: Check if this shouldn't be cfg.getStateCount() instead
        int stateCountPc_0 = cfgs.getNext().getStateCount();

        // Define a list of formulas to be used when calculating Finally
        List<Formula> formulas = new ArrayList<>();
        // The number of the line of code to be used as the CRITICAL section
        int CRITICAL = 7;

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
     * @return
     */
    public Formula ufair(int r) {
        cfgs.reset();

        List<Formula> forAllProcesses = new ArrayList<>();
        for (int i = 0; i < numberOfProcesses; ++i) {
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

    public Formula constructFormulaNotUnknown(Formula ltlPropertyEncoding) {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();
        int stateCountPc_2 = cfgs.getNext().getStateCount();

        ArrayList<Formula> initialProgressValues = new ArrayList<>();
        for (int i = 0; i < numberOfProcesses; ++i) {
            initialProgressValues.add(neg(var(progress[i][0])));
        }

//        Formula initialState = and(encLoc(0, 0, 0, stateCountPc_0), encLoc(1, 0, 0, stateCountPc_1), encPred(0, 0, TRUE_VAL));
        Formula initialState = and(
                encLoc(0, 0, 0, stateCountPc_0),
                encLoc(1, 0, 0, stateCountPc_1),
                encLoc(2, 0, 0, stateCountPc_2),
                encPred(0, 0, TRUE_VAL),
                encPred(1, 0, TRUE_VAL),
                encPred(2, 0, TRUE_VAL),
                encPred(3, 0, TRUE_VAL),
                encPred(4, 0, TRUE_VAL),
                encPred(5, 0, FALSE_VAL),
                // /\ not progress[i][0]
                and(initialProgressValues)
        );

        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        if (checkFairness) {
            initialState = and(initialState, constructLtlEncodingLiveness());
        }

        if (ltlPropertyEncoding == null) return and(initialState, transitionEncoding, TRUE, neg(FALSE), neg(UNKNOWN));

        return and(initialState, transitionEncoding, ltlPropertyEncoding, TRUE, neg(FALSE), neg(UNKNOWN));
    }

    // Specific to Mutex Example 2
    public Formula constructFormulaUnknownMutex2(Formula ltlPropertyEncoding) {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();
        int stateCountPc_2 = cfgs.getNext().getStateCount();

//        Formula initialState = and(
//                encLoc(0, 0, 0, stateCountPc_0),
//                encLoc(1, 0, 0, stateCountPc_1),
//                encPred(0, 0, TRUE_VAL)
//        );
        ArrayList<Formula> initialProgressValues = new ArrayList<>();
        for (int i = 0; i < numberOfProcesses; ++i) {
            initialProgressValues.add(neg(var(progress[i][0])));
        }

        Formula initialState = and(
                encLoc(0, 0, 0, stateCountPc_0),
                encLoc(1, 0, 0, stateCountPc_1),
                encLoc(2, 0, 0, stateCountPc_2),
                encPred(0, 0, TRUE_VAL),
                encPred(1, 0, TRUE_VAL),
                encPred(2, 0, TRUE_VAL),
                encPred(3, 0, TRUE_VAL),
                encPred(4, 0, TRUE_VAL),
                encPred(5, 0, FALSE_VAL),
                // /\ not progress[i][0]
                and(initialProgressValues)
        );

        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        if (checkFairness) {
            initialState = and(initialState, constructLtlEncodingLiveness());
        }

        if (ltlPropertyEncoding == null) return and(initialState, transitionEncoding, TRUE, neg(FALSE), UNKNOWN);

        return and(initialState, transitionEncoding, ltlPropertyEncoding, TRUE, neg(FALSE), UNKNOWN);
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
                System.out.println("Satisfiable");
                System.out.println("True vars:");
                for (Map.Entry<String, Var> e : vars.entrySet()) {
                    if (trueVars.contains(e.getValue())) {
                        System.out.println(e.getKey());
                    }
                }
            } else {
                System.out.println("NOT satisfiable");
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void printVars() {
        System.out.println("Vars:");
        for (Map.Entry<String, Var> e : vars.entrySet()) {
            System.out.println(e.getValue() + " " + e.getKey());
        }
    }

    // TODO: Make difference between these two methods clear - Possibly rename one?

    /**
     * @param cfgs
     * @param bound
     * @return
     */
    private Formula encodeTransition(EnumeratorOfCFGraph cfgs, int bound) {
        cfgs.reset();

        List<Formula> formulas = new ArrayList<>();

        int processCounter = 0;
        while (cfgs.hasNext()) {
            CFGraph cfg = cfgs.getNext();
            Formula formula = encodeTransition(cfg, processCounter, bound);
            formulas.add(formula);
            processCounter += 1;
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
    private Formula encodeTransitions(EnumeratorOfCFGraph cfgs, int bound) {
        List<Formula> formulas = new ArrayList<>();

        for (int i = 0; i < bound; i++) {
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
    private Formula encodeTransition(CFGraph cfg, int process, int bound) {
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
    private Formula encodeTransition(CFGraph cfg, int process, int bound, int r) {
        int stateCount = cfg.getStateCount();
        EnumeratorOfState states = cfg.getStates();
        List<Formula> formulas = new LinkedList<Formula>();
        LogicParser parser = new LogicParser(predMap, vars, TRUE, FALSE, UNKNOWN, bound);

        while (states.hasNext()) {
            State state = states.getNext();

            EnumeratorOfTransition transitions = state.getTransitions();
            while (transitions.hasNext()) {
                List<Formula> currentTransEncoding = new LinkedList<>();
                Transition transition = transitions.getNext();
                Operation operation = transition.getOperation();

                int source = transition.getSource();
                int destination = transition.getDestination();
                Formula locEncoding = and(encLoc(process, source, bound, stateCount), encLoc(process, destination, r, stateCount));
                currentTransEncoding.add(locEncoding);

                // /\ progress[i][k+1]
                currentTransEncoding.add(var(progress[process][r]));

                for (int i = 0; i < numberOfProcesses; i++) {
                    if (i != process) {
                        // TODO Pass in THAT process's stateCount, not this one's
                        Formula idlingEncoding = idleEncoding(i, stateCount, bound);
                        currentTransEncoding.add(idlingEncoding);

                        // /\ not progress[j][k+1]
                        currentTransEncoding.add(neg(var(progress[i][r])));
                    }
                }

                Expression condExpr = operation.getCondExpr();
                if (condExpr != null) {
                    // Has guard
                    String s = condExpr.__toString();
                    ParserHelper parserHelper = new ParserHelper(parser, s).invoke();
                    Formula a = parserHelper.getA();
                    Formula b = parserHelper.getB();

                    String s1 = s.split(",")[1];
                    String bRaw = s1.substring(0, s1.length() - 1).trim();
                    if (bRaw.startsWith("(")) {
                        bRaw = bRaw.substring(1, bRaw.length() - 1);
                    }
                    String str = "not(" + bRaw + ")";
                    String modifiedB = parserHelper.cleanExpression(str);
                    Formula notB = parserHelper.parser.LOGIC_PARSER.parse(modifiedB);
                    // choice(a, b) = (a or not b) and (a or b or unknown)
//                    Formula choiceEncoding = and(or(a, neg(b)), or(a, b, UNKNOWN));
                    // See Definition 2
                    Formula choiceEncoding = and(or(a, notB), or(a, b, UNKNOWN));
                    currentTransEncoding.add(choiceEncoding);
                }

                EnumeratorOfAssignment assignments = operation.getAssignments();

                // TODO Check for all unmodified preds, instead of only when there aren't any next assignments
                if (!assignments.hasNext()) {
                    predMap.forEach((predStr, pred) -> {
                        Formula pUK = var(predVar(pred, bound, false));
                        Formula pUKp1 = var(predVar(pred, r, false));
                        Formula f1 = and(or(neg(pUK), pUKp1), or(pUK, neg(pUKp1)));

                        Formula pTK = var(predVar(pred, bound, true));
                        Formula pTKp1 = var(predVar(pred, r, true));
                        Formula f2 = and(or(neg(pTK), pTKp1), or(pTK, neg(pTKp1)));

                        Formula and = and(f1, f2);
                        currentTransEncoding.add(and);
                    });
                }


                List<Formula> assignmentEncoding = new ArrayList<>();
                while (assignments.hasNext()) {
                    Assignment next = assignments.getNext();
                    Expression assignmentExpression = next.getAssignmentExpression();
                    ParserHelper parserHelper = new ParserHelper(parser, assignmentExpression.__toString()).invoke();
                    Formula a = parserHelper.getA();
                    Formula b = parserHelper.getB();

                    String predStr = next.__toString().split(":=")[0].trim();
                    Integer pred = predMap.get(predStr);

                    Formula f1 = and(a, encPred(pred, r, TRUE_VAL));
                    Formula f2 = and(b, encPred(pred, r, FALSE_VAL));
                    Formula f3 = and(and(neg(a), neg(b)), encPred(pred, r, UNKNOWN_VAL));

                    Formula or = or(f1, f2, f3);
                    assignmentEncoding.add(or);
                    currentTransEncoding.add(or);
                }

                formulas.add(and(currentTransEncoding));
            }
        }
        Formula or = or(formulas);
        return or;
    }

    private Formula idleEncoding(int process, int stateCount, int bound) {
        List<Formula> formulas = new ArrayList<>();

        for (int i = 0; i < stateCount; i++) {
            Formula a = encLoc(process, i, bound, stateCount);
            Formula b = encLoc(process, i, bound + 1, stateCount);
            formulas.add(iff(a, b));
        }

        return and(formulas);
    }

    private Var getNamedVar(String s) {
        // TODO: Possibly use Map's putIfAbsent method here?
        if (vars.get(s) == null) {
            vars.put(s, freshVar());
        }
        return vars.get(s);
    }

    public void test() {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();

        Formula initialState = and(encLoc(0, 0, 0, stateCountPc_0), encLoc(1, 0, 0, stateCountPc_1), encPred(0, 0, TRUE_VAL));

        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        checkSatisfiability(and(initialState, transitionEncoding, TRUE, neg(FALSE), UNKNOWN));
    }

    /**
     * Returns an array of ints where the index of the array corresponds to the process number and the value at that
     * index indicates the number of states for that process
     *
     * @return An array of the state counts
     */
    private int[] getStateCounts() {
        cfgs.reset();
        int[] stateCounts = new int[numberOfProcesses];
        int index = 0;
        while (cfgs.hasNext()) {
            stateCounts[index++] = cfgs.getNext().getStateCount();
        }
        cfgs.reset();
        return stateCounts;
    }

    private Var[][] initialiseProgressFlags() {
        Var[][] progressFlags = new Var[numberOfProcesses][maxBound];
        for (int i = 0; i < numberOfProcesses; ++i) {
            for (int j = 0; j < maxBound; ++j)
                progressFlags[i][j] = freshVar();
        }
        return progressFlags;
    }

    /**
     * Parses a choice expression and exposes the left part of the encoding with getA and the right with getB.
     * Use the invoke method, e.g. ParserHelper parserHelper = new ParserHelper(parser, str).invoke();
     */
    private class ParserHelper {
        private LogicParser parser;
        private String s;
        private Formula a;
        private Formula b;

        public ParserHelper(LogicParser parser, String s) {
            this.parser = parser;
            this.s = s;
        }

        public Formula getA() {
            return a;
        }

        public Formula getB() {
            return b;
        }

        public ParserHelper invoke() {
            String[] split = s.split(",");
            String s1 = cleanExpression(split[0].substring(7));
            String s2 = cleanExpression(split[1].substring(0, split[1].length() - 1));
            a = parser.LOGIC_PARSER.parse(s1);
            b = parser.LOGIC_PARSER.parse(s2);
            return this;
        }

        /**
         * Removes the "choice( )" string and replaces the predicates with their indices (negated predicates become
         * ~index. True and false are replaced with 't' and 'f' respectively
         *
         * @param str The string to be cleaned
         * @return The cleaned string
         */
        @NotNull
        private String cleanChoiceExpression(String str) {
            String s = cleanExpression(str);
            return s.substring(7, s.length() - 1);
        }

        @NotNull
        public String cleanExpression(String str) {
            final String[] s = {str};
            predMap.forEach((k, i) -> s[0] = s[0].replace(k, i.toString()));
            s[0] = s[0].replace("true", "\'t\'");
            s[0] = s[0].replace("false", "\'f\'");

            // Double negation replacement. NB: Has to be in this order.
            // Double negatives of the form not(not(...))
            s[0] = s[0].replaceAll("not\\s*\\(\\s*not\\s*\\((.*)\\)\\)", "$1");

            // Double negatives of the form not( not p )
            s[0] = s[0].replaceAll("not\\s*\\(\\s*not\\s*(.*)\\)", "$1");

            // Replace not pred with ~pred
            s[0] = s[0].replaceAll("not\\s*\\((\\d+)\\)", "\"~$1\"");
            s[0] = s[0].replaceAll("not\\s*(\\d+)", "\"~$1\"");
            return s[0];
        }

        public int findClosingParen(char[] text, int openPos) {
            int closePos = openPos;
            int counter = 1;
            while (counter > 0) {
                char c = text[++closePos];
                if (c == '(') {
                    counter++;
                } else if (c == ')') {
                    counter--;
                }
            }
            return closePos;
        }
    }
}
