/*
package za.ac.up.cs;

import cnf.Formula;
import cnf.Var;
import de.upb.agw.jni.*;
import de.upb.agw.modelchecking.CFGCompiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static cnf.CNF.and;
import static cnf.CNF.or;


public class Main {

//    private static final Map<String, Var> preds = new HashMap<>();
    private static EnumeratorOfCFGraph cfgs;
    private static Map<String, String> map = new HashMap<String, String>();
    private static CParser cParser;
    private static Abstractor abstractor;
    private static boolean[] spotlightVec;
    private static CFGCompiler compiler;
    private static String ctlString;

    private static void initialise(String filename) throws IOException {
        log("Initialising...");

        System.loadLibrary("Spotlight");

        log("Parsing c file...");
        long time = System.currentTimeMillis();
        cParser = new CParser("input/" + filename + ".c");
        long timeUsed = (System.currentTimeMillis() - time);
        log("Finished parsing c file (" + timeUsed + "ms).");

        abstractor = new Abstractor();

        log("Optimising CFGs...");
        time = System.currentTimeMillis();
        cfgs = cParser.getCFGEnumerator();
        log(cParser.getInitCFG().__toString());
        int counter = 0;
        while (cfgs.hasNext()) {
            CFGraph graph = cfgs.getNext();
            graph.reduce();

            log(graph.__toString());
            abstractor.addCFG(graph, false);
            counter++;
        }
        timeUsed = (System.currentTimeMillis() - time);
        log("Finished optimising CFGs (" + timeUsed + "ms).");

        //fill spotlightVec
        spotlightVec = new boolean[counter];
        for (counter = 0; counter < spotlightVec.length; counter++) {
            spotlightVec[counter] = false;
        }

        abstractor.setInitialiserCFG(cParser.getInitCFG());

        System.out.println("Reading predicates...");
        //add predicates
        Vector<Expression> vec = readPredicates("input/" + filename + ".pred");
        for (Expression pred : vec) {
            abstractor.addPredicate(pred);
            pred.delete();
        }

        compiler = null;

        System.out.println("Reading CTL...");
        ctlString = readCTLFormula("input/" + filename + ".ctl");

        System.out.println("Finished initialising.");

    }

    private static String readCTLFormula(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String expr = reader.readLine();
            return expr;
        }
        return null;
    }

    private static Vector<Expression> readPredicates(String file) throws IOException {
        Vector<Expression> vec = new Vector<Expression>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String expr = reader.readLine();
            vec.add(new Expression(expr, true));

            map.put(expr, vec.lastElement().getExpressionCString());
        }
        return vec;
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static void cleanup() {
        cfgs.delete();
        cParser.delete();
        abstractor.delete();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            log("Requires argument [example]");
            return;
        }

        initialise(args[0]);
        log("Traversing...");
//        traverseCfgs();
        CFGraph cfg = cfgs.getNext();
        encodeTransitions(cfg,1);
        cleanup();
    }

*/
/*    private static Formula encodeTransitionsOld(CFGraph cfg, int bound) {
        // See definition 4 in [TGH-Draft-2016]
        List<Formula> formulas = new LinkedList<>();
        EnumeratorOfState states = cfg.getStates();
        EnumeratorOfState states2 = cfg.getStates();


        while (states.hasNext()) {
            while (states2.hasNext()) {
                State state = states.getNext();
                State statePrime = states2.getNext();

                // TODO also AND with X
                Formula f = and(encState(state, bound), encState(statePrime, bound + 1));
                formulas.add(f);
            }
        }

        return or(formulas);
    }*//*


//    private static Formula encodeTransitions(CFGraph cfg, int bound) {
//        // See definition 4 in [TGH-Draft-2016]
//        int stateCount = cfg.getStateCount();
//        EnumeratorOfExpression predicates = abstractor.getPredicates();
//        int numberOfPredicates = predicates.getNumberofElements();
//
//        System.out.println(numberOfPredicates);
//        // For all states s
//        // For all states s'
//        for (int i = 0; i < numberOfPredicates; i++) {
//            for (int j = 0; j <= 2; j++) {
//                System.out.println(i + " " + j);
//            }
//        }
//
//        return null;
//    }

    private static Formula encState(KState state, int bound) {
        // See definition 3 [TGH-Draft-2016]
        List<Formula> list = new LinkedList<>();

        // TODO
        int loc = 0;

//        preds.forEach((s, var) -> list.add(and(encPred(sOfPred(state, var), bound), encLoc(sOfLoc(state, loc), bound))));

        return and(list);
    }

    private static int sOfLoc(KState state, int loc) {
        return 0;
    }

    private static Var sOfPred(KState state, Var var) {
        return null;
    }

    //TODO
    private static Formula encLoc(int loc, int bound) {
        return null;
    }

    //TODO
    private static Formula encPred(Var var, int bound) {
        return null;
    }

    private static void traverseCfgs() {
        cfgs.reset();

        while (cfgs.hasNext()) {
            CFGraph cfg = cfgs.getNext();
            log(cfg.getProgramNumber() + ":");
            EnumeratorOfState states = cfg.getStates();
            while (states.hasNext()) {
                State state = states.getNext();
                EnumeratorOfTransition transitions = state.getTransitions();
                while (transitions.hasNext()) {
                    Transition transition = transitions.getNext();
                    log("[" + transition.getSource() + " " + transition.getDestination() + "]");
                }
            }
        }


    }

}
*/
