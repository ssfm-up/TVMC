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
    private final EnumeratorOfExpression predicates;
    private final CFGraph cfg;
    private final Map<String, Var> vars = new HashMap<String, Var>();
    // Used to lookup the number of the predicate
    private final Map<String, Integer> predMap = new HashMap<String, Integer>();
    private final int maxBound;
    private final int numberOfLocs;
    private final int numberOfPreds;
//    private List<KState> states;

    public ThreeValuedModelChecker(EnumeratorOfExpression predicates, CFGraph cfg, int maxBound) {
        this.predicates = predicates;
        this.cfg = cfg;

        this.numberOfLocs = cfg.getStateCount();
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
            System.out.println("p" + i + " : " + next.__toString());
            i++;
        }
        predicates.reset();

    }

    private Formula encState(KState state, int bound) {
        // See definition 3 [TGH-Draft-2016]

        List<Formula> formulas = new LinkedList<Formula>();

        // For each pred i in Pred
        for (int i = 0; i < numberOfPreds; i++) {
            Integer stateAtI = state.predVals.get(i);
            Formula predEncoding = encPred(i, bound, stateAtI);
            formulas.add(predEncoding);
        }
        // AND enc(l = s(l))j
        Formula locEncoding = encLoc(state.locationVal, bound);
        formulas.add(locEncoding);

        return and(formulas);
    }


    // See definition 2 [TGH-Draft-2016]
    private Formula encLoc(int loc, int bound) {
        int numOfBinaryDigits = (int) Math.ceil(Math.log(numberOfLocs) / Math.log(2.0));

        List<Formula> formulas = new LinkedList<Formula>();

        // Convert loc to binary and then AND each binary digit together
        int oldLoc;
        for (int i = numOfBinaryDigits; i > 0; i--) {
            oldLoc = loc;
            loc /= 2;
            int remainder = oldLoc - loc * 2;
            if (remainder == 0) {
                formulas.add(neg(var(locVar(i - 1, bound))));
            } else {
                assert remainder == 1;
                formulas.add(var(locVar(i - 1, bound)));
            }
        }

        return and(formulas);
    }

    private Var locVar(int loc, int bound) {
        return getNamedVar("l_" + loc + "_" + bound);
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

    public void test() {
        System.out.println("---------------------------------------");
        System.out.println("Number of predicates: " + numberOfPreds);

        Formula initialState = and(encLoc(0, 0), encPred(0, 0, TRUE_VAL));
        System.out.println("Initial state: " + initialState);

        Formula t0_1 = encodeTransitions(cfg, 0);
        System.out.println("Transition encoding: " + t0_1);
        Formula t1_2 = encodeTransitions(cfg, 1);
        Formula t2_3 = encodeTransitions(cfg, 2);

        Formula encodingNotUnknown = and(initialState, t0_1, t1_2, t2_3, TRUE, neg(FALSE), neg(UNKNOWN));
        Formula encodingUnknown = and(initialState, t0_1, t1_2, t2_3, TRUE, neg(FALSE), UNKNOWN);
//        Formula encoding2 = and(initialState, t0_1);
        Formula encoding3 = and(t0_1, t1_2, t2_3, TRUE, neg(FALSE), UNKNOWN);
//        Formula ltlPropertyEncoding = ;

        System.out.println("Vars:");
        for (Map.Entry<String, Var> e : vars.entrySet()) {
            System.out.println(e.getValue() + " " + e.getKey());
        }

//        String cnfFormula = cnfDIMACS(encoding);
        Formula cnfFormula = cnf(encodingNotUnknown);
        System.out.println(cnfFormula);
        try {
            Set<Var> trueVars = CNF.satisfiable(cnfFormula);
            if (trueVars != null) {
                System.out.println("True vars:");
                for (Map.Entry<String, Var> e : vars.entrySet()) {
                    if (trueVars.contains(e.getValue())) {
                        System.out.println(e.getKey());
                    }
                }
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        }

//        predicates.reset();
//        while (predicates.hasNext()) {
//            Expression next = predicates.getNext();
//            System.out.println(next.__toString());
//        }
    }

    /**
     * See definition 3 (Encoding of Transitions)
     *
     * @param cfg
     * @param bound
     * @return
     */
    private Formula encodeTransitions(CFGraph cfg, int bound) {
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
                System.out.println("---------------------------------------");
                System.out.println("Op:   " + operation.__toString());

                int source = transition.getSource();
                int destination = transition.getDestination();
//                System.out.println("source = " + source);
//                System.out.println("destination = " + destination);
                Formula locEncoding = and(encLoc(source, bound), encLoc(destination, bound + 1));
                System.out.println("locEncoding = " + locEncoding);
                currentTransEncoding.add(locEncoding);

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
//                    System.out.println("str = " + str);
                    String modifiedB = parserHelper.cleanExpression(str);
//                    System.out.println("modifiedB = " + modifiedB);
                    Formula notB = parserHelper.parser.LOGIC_PARSER.parse(modifiedB);
                    // choice(a, b) = (a or not b) and (a or b or unknown)
//                    Formula choiceEncoding = and(or(a, neg(b)), or(a, b, UNKNOWN));
                    Formula choiceEncoding = and(or(a, notB), or(a, b, UNKNOWN));
                    currentTransEncoding.add(choiceEncoding);
                    System.out.println("Choice encoding: " + choiceEncoding);
                }

                EnumeratorOfAssignment assignments = operation.getAssignments();

                // TODO Check for all unmodified preds, instead of only when there aren't any next assignments
                if (!assignments.hasNext()) {
                    List<Formula> assignmentEncoding = new ArrayList<>();
                    predMap.forEach((predStr, pred) -> {
                        Formula pUK = var(predVar(pred, bound, false));
                        Formula pUKp1 = var(predVar(pred, bound + 1, false));
                        Formula f1 = and(or(neg(pUK), pUKp1), or(pUK, neg(pUKp1)));

                        Formula pTK = var(predVar(pred, bound, true));
                        Formula pTKp1 = var(predVar(pred, bound + 1, true));
                        Formula f2 = and(or(neg(pTK), pTKp1), or(pTK, neg(pTKp1)));

                        Formula and = and(f1, f2);
                        assignmentEncoding.add(and);
                        currentTransEncoding.add(and);
                    });
                    System.out.println("assignmentEncoding = " + assignmentEncoding);
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

                if (assignmentEncoding.size() != 0) {
                    System.out.println("assignmentEncoding = " + assignmentEncoding);
                }

                formulas.add(and(currentTransEncoding));
            }
        }
        return or(formulas);
    }

    Var getNamedVar(String s) {
        Var x = vars.get(s);
        if (x == null) {
            x = freshVar();
            vars.put(s, x);
        }
        return x;
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
//            System.out.println(split[0].substring(7));
            String s1 = cleanExpression(split[0].substring(7));
            String s2 = cleanExpression(split[1].substring(0, split[1].length() - 1));
//            System.out.println("s1 = " + s1);
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
//            System.out.println(s[0].substring(7, s[0].length() - 1));
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
