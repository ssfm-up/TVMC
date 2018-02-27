package za.ac.up.cs;

import cnf.CNF;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static cnf.CNF.and;

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

        Solver<DataStructureFactory> baseSolver = SolverFactory.newMiniLearningHeap();
        Solver<DataStructureFactory> stepSolver = SolverFactory.newMiniLearningHeap();
        boolean shouldResetStep = true;
        boolean shouldResetBase = true;
        final boolean printTrueVars = true;

        Formula baseCase = null;
        Formula step = null;
        UnboundedModelChecker modelChecker = new UnboundedModelChecker(0);

        for (int k = 0; k < maxBound; k++) {
            System.out.println("k = " + k);
            modelChecker.setMaxBound(k);

            boolean bUnknown;
            boolean bNotUnknown;
            boolean baseRequiresRefinement = false;

            String path = basePath + predBase + "P.json";
            CFG cfg = Helpers.readCfg(path);
            final int stateCount = cfg.getProcess(0).getStateCount();

            final SafeLocEncodingFunction safeAllAtLocFunction = modelChecker::safeAnyPairAtLoc;

            do {
                path = basePath + predBase + "P.json";
                System.out.println("base " + predBase + "p");
                modelChecker.setCfgs(Helpers.readCfg(path));

                if (!baseRequiresRefinement && shouldResetBase) {
                    System.out.println("Resetting base case...");
                    baseSolver = SolverFactory.newMiniLearningHeap();
                    Formula ltlEncodingBase = modelChecker.generateSafetyEncodingFormula(k, loc, processes, stateCount, safeAllAtLocFunction);

                    baseCase = modelChecker.constructBaseCaseFormula(ltlEncodingBase);
                    CNF.addClauses(baseSolver, baseCase);

                    shouldResetBase = false;
                } else if (baseRequiresRefinement) {
                    //baseRequiresRefinement && shouldResetBase
//                    System.out.println("Adding to base case...");
                    baseSolver = addLearntClauses(baseSolver);

                    Formula ltlEncodingBase = modelChecker.generateSafetyEncodingFormula(k, loc, processes, stateCount, safeAllAtLocFunction);
                    baseCase = modelChecker.constructBaseCaseFormula(ltlEncodingBase);
                    CNF.addClauses(baseSolver, baseCase);
                } else {
                    // !baseRequiresRefinement && !shouldResetBase
                    final Formula ltlAddition = modelChecker.generateAdditiveSafetyEncoding(k, loc, processes, stateCount, safeAllAtLocFunction);
                    final Formula addition = modelChecker.constructAdditiveBaseCase(k, ltlAddition);
                    CNF.addClauses(baseSolver, addition);
                }

                final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k).number;
                bUnknown = modelChecker.checkSatisfiability(baseSolver, new VecInt(new int[]{3, -zVarNum}), printTrueVars);
                bNotUnknown = modelChecker.checkSatisfiability(baseSolver, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);

                // true, false ==> Unknown, so add predicate
                if (bUnknown && !bNotUnknown) {
                    predBase += 1;
                    baseRequiresRefinement = true;
                    shouldResetBase = true;
                } else {
                    baseRequiresRefinement = false;
                }
            } while (baseRequiresRefinement);

            if (bUnknown) return true; // true, true ==> Error reachable
            else if (bNotUnknown) { // false, true ==> invalid
                System.out.println("Error: (bUnknown, bNotUnknown) = (false, true)");
                System.exit(1);
            } else {
                // false, false ==> Proceed to induction step, k + 1
                boolean sUnknown;
                boolean sNotUnknown;
                boolean stepRequiresRefinement = false;

                do {
                    String stepPath = basePath + predStep + "P.json";
                    System.out.println();
                    System.out.println("step k=" + (k + 1) + " " + predStep + "p");
                    modelChecker.setCfgs(Helpers.readCfg(stepPath));
                    modelChecker.setMaxBound(k + 1);


                    if (!stepRequiresRefinement && shouldResetStep) {
                        stepSolver = SolverFactory.newMiniLearningHeap();

                        Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        step = modelChecker.getStepFormula(ltlEncoding, processes, stateCount);
                        CNF.addClauses(stepSolver, step);

                        shouldResetStep = false;
                    } else if (stepRequiresRefinement) {
                        //stepRequiresRefinement && shouldResetStep
                        stepSolver = addLearntClauses(stepSolver);
                        Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        step = modelChecker.getStepFormula(ltlEncoding, processes, stateCount);
                        CNF.addClauses(stepSolver, step);
                    } else {
                        // !stepRequiresRefinement && !shouldResetStep
                        final Formula ltlAddition = modelChecker.generateAdditiveSafetyEncoding(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        final Formula addition = modelChecker.constructAdditiveStepCase(k + 1, ltlAddition, processes, stateCount);
                        CNF.addClauses(stepSolver, addition);
                    }

                    final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k + 1).number;
                    sUnknown = modelChecker.checkSatisfiability(stepSolver, new VecInt(new int[]{3, -zVarNum}), printTrueVars);
                    sNotUnknown = modelChecker.checkSatisfiability(stepSolver, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);

                    if (sUnknown && !sNotUnknown) {
                        predStep += 1;
                        shouldResetStep = true;
                        stepRequiresRefinement = true;
                    } else {
                        stepRequiresRefinement = false;
                    }
                } while (stepRequiresRefinement);

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
