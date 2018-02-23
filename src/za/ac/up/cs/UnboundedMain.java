package za.ac.up.cs;

import cnf.Formula;
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
//        iterateLocations();
        final long startTime = System.nanoTime();
        boolean result = start(300, 2, 3);
        System.out.println("result = " + result);
        final double duration = (System.nanoTime() - startTime) / 1e9;
        System.out.println("duration = " + duration);
    }

    private static void iterateLocations() throws IOException {
        List<Double> times = new ArrayList<>();
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final long startTime = System.nanoTime();
            boolean result = start(300, i, 4);
            final double duration = (System.nanoTime() - startTime) / 1e9;
            System.out.println("result = " + result);
            System.out.println("duration = " + duration);
            times.add(duration);
            results.add(result);
        }

        for (int i = 0; i < times.size(); i++) {
            System.out.println("loc: " + i + ", time: " + times.get(i) + ", result: " + results.get(i));
        }
    }

    /**
     * Iterates up to maxBound and checks whether an error state is reachable at loc
     * using induction.
     *
     * @param maxBound  The maximum bound that will be considered
     * @param loc       The location for which to check whether an error is reachable
     * @param processes The number of processes
     * @return true if an error state is reachable, false otherwise
     * @throws IOException
     */
    static boolean start(int maxBound, int loc, int processes) throws IOException {
        int predBase = 0;
        int predStep = 0;
        final String basePath = "examples/" + processes + "philosophers/" + processes + "Phil";

        Properties config = Helpers.loadConfigurationFile();
        Solver<DataStructureFactory> baseSolver = SolverFactory.newMiniLearningHeap();
        Solver<DataStructureFactory> stepSolver = SolverFactory.newMiniLearningHeap();
        boolean shouldResetStep = false;
        boolean shouldResetBase = false;

        for (int k = 0; k < maxBound; k++) {
            System.out.println("k = " + k);

            boolean bUnknown = false;
            boolean bNotUnknown = false;
            boolean baseRequiresRefinement = true;

            String path = basePath + predBase + "P.json";
            CFG cfg = Helpers.readCfg(path);
            final int stateCount = cfg.getProcess(0).getStateCount();
            UnboundedModelChecker modelChecker = new UnboundedModelChecker(cfg, k, config);

            final SafeLocEncodingFunction safeAllAtLocFunction = modelChecker::safeAllAtLoc;
            Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k, loc, processes, stateCount, safeAllAtLocFunction);

            while (baseRequiresRefinement) {
                baseSolver = addLearntClauses(baseSolver);
                path = basePath + predBase + "P.json";
                System.out.println("path = " + path);
                modelChecker.setCfgs(Helpers.readCfg(path));

                Formula baseCase = modelChecker.getBaseCaseFormula(ltlEncoding);

                final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k).number;
                bUnknown = modelChecker.checkSatisfiability(baseCase, baseSolver, new VecInt(new int[]{3, -zVarNum}));
                bNotUnknown = modelChecker.checkSatisfiability(baseCase, baseSolver, new VecInt(new int[]{-3, -zVarNum}));

                // true, false ==> Unknown, so add predicate
                if (bUnknown && !bNotUnknown) {
                    predBase += 1;
                    shouldResetBase = true;
                } else {
                    baseRequiresRefinement = false;
                    if (shouldResetBase) baseSolver = SolverFactory.newMiniLearningHeap();
                    shouldResetBase = false;
                }
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

                String stepPath = basePath + predStep + "P.json";

                CFG cfgStep = Helpers.readCfg(stepPath);
                modelChecker = new UnboundedModelChecker(cfgStep, k + 1, config);

                ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, stateCount, safeAllAtLocFunction);

                while (stepRequiresRefinement) {
                    stepSolver = addLearntClauses(stepSolver);

                    stepPath = basePath + predStep + "P.json";
                    System.out.println("stepPath = " + stepPath);
                    modelChecker.setCfgs(Helpers.readCfg(stepPath));

                    Formula step = modelChecker.getStepFormula(ltlEncoding, processes, stateCount);

                    final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k + 1).number;
                    sUnknown = modelChecker.checkSatisfiability(step, stepSolver, new VecInt(new int[]{3, -zVarNum}));
                    sNotUnknown = modelChecker.checkSatisfiability(step, stepSolver, new VecInt(new int[]{-3, -zVarNum}));

                    if (sUnknown && !sNotUnknown) {
                        predStep += 1;
                        shouldResetStep = true;
                    } else {
                        stepRequiresRefinement = false;
                        if (shouldResetStep) stepSolver = SolverFactory.newMiniLearningHeap();
                        shouldResetStep = false;
                    }
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

    private static void printStats(Solver solver) {
        PrintWriter out = new PrintWriter(System.out);
        solver.printLearntClausesInfos(out, "Learnt clause: ");
        out.flush();
        System.out.println();

        IVec learnedConstraints = solver.getLearnedConstraints();
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
