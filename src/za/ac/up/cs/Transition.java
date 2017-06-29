package za.ac.up.cs;

import cnf.Formula;

import java.util.ArrayList;
import java.util.List;

import static cnf.CNF.*;

public class Transition {
    private int source;
    private int destination;
    private List<Assignment> assignments;
    private String guard;

    public Transition() {
    }

    public Transition(int source, int destination, String guard, List<Assignment> assignments) {
        this.source = source;
        this.destination = destination;
        this.guard = guard;
        this.assignments = assignments;
    }

    int getSource() {
        return source;
    }

    int getDestination() {
        return destination;
    }

    Formula getGuardEncoding(ThreeValuedModelChecker mc, LogicParser parser) {
        String s = guard;

        ParserHelper parserHelper = new ParserHelper(parser, s, mc.predMap).invoke();
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
        Formula choiceEncoding = and(or(a, notB), or(a, b, ThreeValuedModelChecker.UNKNOWN));
        return choiceEncoding;
    }

    List<Formula> getAssignmentEncodings(ThreeValuedModelChecker mc, LogicParser parser, int bound, int r) {
        List<Formula> assignmentEncodings = new ArrayList<>();
        List<Integer> assignedPredicates = new ArrayList<>();
        for (Assignment assignment : getAssignments()) {
            ParserHelper parserHelper = new ParserHelper(parser, assignment.getRHS(), mc.predMap).invoke();
            Formula a = parserHelper.getA();
            Formula b = parserHelper.getB();

            Integer pred = assignment.getPredicate();
            assignedPredicates.add(pred);

            Formula f1 = and(a, mc.encodePredicate(pred, r, ThreeValuedModelChecker.TRUE_VAL));
            Formula f2 = and(b, mc.encodePredicate(pred, r, ThreeValuedModelChecker.FALSE_VAL));
            Formula f3 = and(and(neg(a), neg(b)), mc.encodePredicate(pred, r, ThreeValuedModelChecker.UNKNOWN_VAL));

            Formula or = or(f1, f2, f3);
            assignmentEncodings.add(or);
        }
        assignmentEncodings.addAll(idlePredicateEncoding(mc, assignedPredicates, bound, r));
        return assignmentEncodings;
    }

    private List<Formula> idlePredicateEncoding(ThreeValuedModelChecker mc, List<Integer> excludedPreds, int bound, int r) {
        List<Formula> encoding = new ArrayList<>();
        mc.predMap.forEach((predStr, pred) -> {
            if (!excludedPreds.contains(pred)) {
                //System.out.println("Adding idle encoding for: " + pred);
                Formula pUK = var(mc.predVar(pred, bound, false));
                Formula pUKp1 = var(mc.predVar(pred, r, false));
                Formula f1 = and(or(neg(pUK), pUKp1), or(pUK, neg(pUKp1)));

                Formula pTK = var(mc.predVar(pred, bound, true));
                Formula pTKp1 = var(mc.predVar(pred, r, true));
                Formula f2 = and(or(neg(pTK), pTKp1), or(pTK, neg(pTKp1)));
                encoding.add(and(f1, f2));
            }
        });
        return encoding;
    }

    void rename(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    List<Assignment> getAssignments() {
        return assignments;
    }

    boolean hasGuard() {
        return (guard != null);
    }

}