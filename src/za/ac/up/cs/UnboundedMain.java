package za.ac.up.cs;

import cnf.Formula;
import org.codehaus.jparsec.functors.Pair;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.core.Constr;
import org.sat4j.minisat.core.DataStructureFactory;
import org.sat4j.minisat.core.Solver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.IteratorInt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class UnboundedMain {

    public static void main(String[] args) throws IOException {
        boolean result = start(20, 2, 10, 4);
        System.out.println("result = " + result);
    }

    /**
     * Iterates up to maxBound and checks whether an error state is reachable at loc
     * using induction.
     *
     * @param maxBound The maximum bound that will be considered
     * @param loc The location for which to check whether an error is reachable
     * @param processes The number of processes
     * @param numberOfLocs The number of locations in the CFG @TODO automatically figure this out
     * @return true if an error state is reachable, false otherwise
     * @throws IOException
     */
    static boolean start(int maxBound, int loc, int processes, int numberOfLocs) throws IOException {
        int predBase = 0;
        int predStep = 0;

        Properties config = Helpers.loadConfigurationFile();

        for (int k = 0; k < maxBound; k++) {
            System.out.println("k = " + k);

            boolean bUnknown = false;
            boolean bNotUnknown = false;
            boolean baseRequiresRefinement = true;

            String path = "examples/10philosophers/10Phil" + predBase + "P.json";
            CFG cfg = Helpers.readCfg(path);
            UnboundedModelChecker modelChecker = new UnboundedModelChecker(cfg, k, config);
            Solver<DataStructureFactory> solver = SolverFactory.newMiniLearningHeap();
            Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k, loc, processes, numberOfLocs);

            while (baseRequiresRefinement) {
                solver = addLearntClauses(solver);
                path = "examples/10philosophers/10Phil" + predBase + "P.json";
                System.out.println("path = " + path);
                modelChecker.setCfgs(Helpers.readCfg(path));

                Formula baseCase = modelChecker.getBaseCaseFormula(ltlEncoding);

                bUnknown = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(new int[]{3}));
                bNotUnknown = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(new int[]{-3}));

                // true, false ==> Unknown, so add predicate
                if (bUnknown && !bNotUnknown) predBase += 1;
                else baseRequiresRefinement = false;
            }

            if (bUnknown) return true; // true, true ==> Error reachable
            else if (bNotUnknown) { // false, true ==> invalid
                System.out.println("Error: (bUnknown, bNotUnknown) = (false, true)");
                System.exit(1);
            } else {
                // false, false ==> Proceed to induction step, k + 1
                boolean sUnknown = false;
                boolean sNotUnknown = false;
                boolean stepRequiresRefinement = true;

                String stepPath = "examples/10philosophers/10Phil" + predStep + "P.json";

                CFG cfgStep = Helpers.readCfg(stepPath);
                modelChecker = new UnboundedModelChecker(cfgStep, k + 1, config);

                ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, numberOfLocs);

                solver = SolverFactory.newMiniLearningHeap();

                while (stepRequiresRefinement) {
                    solver = addLearntClauses(solver);

                    stepPath = "examples/10philosophers/10Phil" + predStep + "P.json";
                    System.out.println("stepPath = " + stepPath);
                    modelChecker.setCfgs(Helpers.readCfg(stepPath));

                    Formula step = modelChecker.getStepFormula(ltlEncoding, processes, numberOfLocs);

                    sUnknown = modelChecker.checkSatisfiability(step, solver, new VecInt(new int[]{3}));
                    sNotUnknown = modelChecker.checkSatisfiability(step, solver, new VecInt(new int[]{-3}));

                    if (sUnknown && !sNotUnknown) predStep += 1;
                    else stepRequiresRefinement = false;
                }

                if (!sUnknown && !sNotUnknown) return false;

            }
        }

        System.out.println("Could not determine if error is reachable with the current max bound.");
        System.exit(1);
        return false;
    }

    private static Solver<DataStructureFactory> addLearntClauses(Solver<DataStructureFactory> solver) {
        Solver<DataStructureFactory> solver2 = SolverFactory.newMiniLearningHeap();
        IVec<Constr> learnedConstraints = solver.getLearnedConstraints();
        Iterator<Constr> iterator = learnedConstraints.iterator();

        while (iterator.hasNext()) {
            Constr next = iterator.next();
            solver2.learn(next);
        }
        return solver2;
    }

    private static void checkSafety(int maxBound, UnboundedModelChecker modelChecker, Solver solver, int loc, int processes, int numberOfLocs) {
        // Safety encoding
        Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(maxBound, loc, processes, numberOfLocs);
        System.out.println("ltlEncoding = " + ltlEncoding);


        Formula baseCase = modelChecker.getBaseCaseFormula(ltlEncoding);
//        System.out.println("baseCase = " + baseCase);
        System.out.println();

        System.out.println("==== UNKNOWN FORMULA ====");
        ArrayList<Integer> unknownAssumptionList = new ArrayList<>();
        unknownAssumptionList.add(3);
        int[] unknownAssumptions = unknownAssumptionList.stream().mapToInt(x -> x).toArray();
        boolean bUnknown = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(unknownAssumptions));
//        modelChecker.printVars();
        System.out.println("Is satisfiable? = " + bUnknown);

//        printStats(solver);

        System.out.println();
        System.out.println("==== NOT UNKNOWN FORMULA ====");
        ArrayList<Integer> notUnknownAssumptionList = new ArrayList<>();
        notUnknownAssumptionList.add(-3);
        int[] notUnknownAssumptions = notUnknownAssumptionList.stream().mapToInt(x -> x).toArray();

        boolean bNotUnknown = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(notUnknownAssumptions));
//        modelChecker.printVars();
        System.out.println("Is satisfiable? = " + bNotUnknown);

//        printStats(solver);

        System.out.println();

        System.out.println("Unknown formula satisfiable: " + bUnknown);
        System.out.println("Not unknown formula satisfiable: " + bNotUnknown);

        Formula step = modelChecker.getStepFormula(ltlEncoding, processes, numberOfLocs);
//        System.out.println("step = " + step);
    }

    private static void printStats(Solver solver) {
        PrintWriter out = new PrintWriter(System.out);
        solver.printLearntClausesInfos(out, "Learnt clause: ");
        out.flush();
        System.out.println();

        IVec learnedConstraints = solver.getLearnedConstraints();
//                IteratorInt iterator = outLearnt.iterator();
        Iterator iterator = learnedConstraints.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            System.out.println("Learned constraint: " + next);
        }

        IVecInt outLearnt = solver.getOutLearnt();
        IteratorInt outLearntIterator = outLearnt.iterator();
        while (outLearntIterator.hasNext()) {
            System.out.println("solver.getOutLearnt() : " + outLearntIterator.next());
        }

        Map stat = solver.getStat();

        stat.forEach((o, o2) -> System.out.println(o + " --- " + o2));
    }

    private static void printUsage() {
        System.out.println("USAGE: inputfile.json <maxBound>");
    }

}
