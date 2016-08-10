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
    private final Map<String, Var> vars = new HashMap<>();
    // Used to lookup the number of the predicate
    private final Map<String, Integer> predMap = new HashMap<>();
    private final int maxBound;
    private final int numberOfPreds;
    private final int numberOfProcesses;

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
    }

    // See definition 2 [TGH-Draft-2016]
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

    // See definition 2 [TGH-Draft-2016]
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

    Var predVar(int pred, int bound, boolean known) {
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

    public Formula constructFormulaNotUnknown(Formula ltlPropertyEncoding) {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();

        Formula initialState = and(encLoc(0, 0, 0, stateCountPc_0), encLoc(1, 0, 0, stateCountPc_1), encPred(0, 0, TRUE_VAL));
        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        return and(initialState, transitionEncoding, ltlPropertyEncoding, TRUE, neg(FALSE), neg(UNKNOWN));
    }

    public Formula constructFormulaUnknown(Formula ltlPropertyEncoding) {
        cfgs.reset();
        int stateCountPc_0 = cfgs.getNext().getStateCount();
        int stateCountPc_1 = cfgs.getNext().getStateCount();

        Formula initialState = and(encLoc(0, 0, 0, stateCountPc_0), encLoc(1, 0, 0, stateCountPc_1), encPred(0, 0, TRUE_VAL));

        Formula transitionEncoding = encodeTransitions(cfgs, maxBound);

        return and(initialState, transitionEncoding, ltlPropertyEncoding, TRUE, neg(FALSE), UNKNOWN);
    }


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

    private void printVars() {
        System.out.println("Vars:");
        for (Map.Entry<String, Var> e : vars.entrySet()) {
            System.out.println(e.getValue() + " " + e.getKey());
        }
    }

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

    private Formula encodeTransitions(EnumeratorOfCFGraph cfgs, int bound) {
        List<Formula> formulas = new ArrayList<>();

        for (int i = 0; i < bound; i++) {
            formulas.add(encodeTransition(cfgs, i));
        }

        return and(formulas);
    }

    /**
     * Encodes the transitions of a single CFG
     * See definition 3 (Encoding of Transitions)
     *
     * @param cfg
     * @param process
     * @param bound   @return
     */
    private Formula encodeTransition(CFGraph cfg, int process, int bound) {
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
                Formula locEncoding = and(encLoc(process, source, bound, stateCount), encLoc(process, destination, bound + 1, stateCount));
                currentTransEncoding.add(locEncoding);

                for (int i = 0; i < numberOfProcesses; i++) {
                    if (i != process) {
                        // TODO Pass in THAT process's stateCount, not this one's
                        Formula idlingEncoding = idleEncoding(i, stateCount, bound);
                        currentTransEncoding.add(idlingEncoding);
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
                    Formula choiceEncoding = and(or(a, notB), or(a, b, UNKNOWN));
                    currentTransEncoding.add(choiceEncoding);
                }

                EnumeratorOfAssignment assignments = operation.getAssignments();

                // TODO Check for all unmodified preds, instead of only when there aren't any next assignments
                if (!assignments.hasNext()) {
                    predMap.forEach((predStr, pred) -> {
                        Formula pUK = var(predVar(pred, bound, false));
                        Formula pUKp1 = var(predVar(pred, bound + 1, false));
                        Formula f1 = and(or(neg(pUK), pUKp1), or(pUK, neg(pUKp1)));

                        Formula pTK = var(predVar(pred, bound, true));
                        Formula pTKp1 = var(predVar(pred, bound + 1, true));
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

                    Formula f1 = and(a, encPred(pred, bound + 1, TRUE_VAL));
                    Formula f2 = and(b, encPred(pred, bound + 1, FALSE_VAL));
                    Formula f3 = and(and(neg(a), neg(b)), encPred(pred, bound + 1, UNKNOWN_VAL));

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

    Var getNamedVar(String s) {
        Var x = vars.get(s);
        if (x == null) {
            x = freshVar();
            vars.put(s, x);
        }
        return x;
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
