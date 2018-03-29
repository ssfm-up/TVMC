package za.ac.up.cs;

import cnf.CNF;
import cnf.Formula;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.core.Constr;
import org.sat4j.minisat.core.DataStructureFactory;
import org.sat4j.minisat.core.Solver;
import org.sat4j.specs.IVec;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnboundedMain {
    // These track the number of shared constraints used for the respective cases
    static List<Integer> sharedBaseBound = new ArrayList<>();
    static List<Integer> sharedBaseRefine = new ArrayList<>();
    static List<Integer> sharedBasePlusToMinus = new ArrayList<>();

    static List<Integer> sharedStepBound = new ArrayList<>();
    static List<Integer> sharedStepRefine = new ArrayList<>();
    static List<Integer> sharedStepMinusToPlus = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        iterateLocations();
        final long startTime = System.nanoTime();
        boolean result = start(300, 2, 3);
        System.out.println("result = " + result);
        final double duration = (System.nanoTime() - startTime) / 1e9;
        System.out.println("duration = " + duration);

        System.out.println("sharedBaseBound = " + sharedBaseBound);
        System.out.println("sharedBaseRefine = " + sharedBaseRefine);
        System.out.println("sharedBasePlusToMinus = " + sharedBasePlusToMinus);
        System.out.println("sharedStepBound = " + sharedStepBound);
        System.out.println("sharedStepRefine = " + sharedStepRefine);
        System.out.println("sharedStepMinusToPlus = " + sharedStepMinusToPlus);
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
        final String basePath = "examples/" + processes + "philosophers/" + processes + "Phil";

        final boolean printTrueVars = false;
        final boolean printStats = false;
        final boolean printLearnedConstraints = false;
        final boolean printSatTimes = false;
        // Whether to calculate the use of shared constraints
        final boolean trackSharing = true;
        final boolean kSharing = true;
        final boolean plusMinSharing = true;
        final boolean refinementSharing = true;
        final boolean stepWithInit = true;

        // Constraints more than this number will not be learned
        final int maxLengthForRefinementConstraint = 40000;


        int predBase = 0;
        int predStep = 0;

        SolverContainer baseSolver = new SolverContainer(SolverFactory.newMiniLearningHeap(), SolverFactory.newMiniLearningHeap());
        SolverContainer stepSolver = new SolverContainer(SolverFactory.newMiniLearningHeap(), SolverFactory.newMiniLearningHeap());

        boolean shouldResetStep = true;
        boolean shouldResetBase = true;

        UnboundedModelChecker modelChecker = new UnboundedModelChecker(0);
        final SafeLocEncodingFunction safeAllAtLocFunction = modelChecker::safeAnyPairAtLoc;

        for (int k = 0; k < maxBound; k++) {
            System.out.println("k = " + k);
            modelChecker.setMaxBound(k);

            boolean bUnknown;
            boolean bNotUnknown;
            boolean baseRequiresRefinement = false;

            String path = basePath + predBase + "P.json";
            CFG cfg = Helpers.readCfg(path);
            final int stateCount = cfg.getProcess(0).getStateCount();

            do {
                path = basePath + predBase + "P.json";
                System.out.println("base " + predBase + "p");
                modelChecker.setCfgs(Helpers.readCfg(path));

                Formula baseCase;
                if (!baseRequiresRefinement && shouldResetBase) {
                    // Next k case, but predicate refinement happened so formula gets reset
                    Formula ltlEncodingBase = modelChecker.generateSafetyEncodingFormula(k, loc, processes, stateCount, safeAllAtLocFunction);

                    baseCase = modelChecker.constructBaseCaseFormula(ltlEncodingBase);

                    nextKResetFormulaCase(plusMinSharing, baseSolver, baseCase);

                    modelChecker.clearAssumptions();

                    shouldResetBase = false;
                } else if (baseRequiresRefinement) {
                    // baseRequiresRefinement && shouldResetBase
                    // Predicate refinement case
                    Formula ltlEncodingBase = modelChecker.generateSafetyEncodingFormula(k, loc, processes, stateCount, safeAllAtLocFunction);
                    baseCase = modelChecker.constructBaseCaseFormula(ltlEncodingBase);

                    refinementCase(plusMinSharing, refinementSharing, maxLengthForRefinementConstraint, baseSolver, baseCase);
                    if (trackSharing)
                        sharedBaseRefine.add(baseSolver.solverMin.getLearnedConstraints().size() + baseSolver.solverPlus.getLearnedConstraints().size());
                } else {
                    // !baseRequiresRefinement && !shouldResetBase
                    // Next k case and predicate refinement did not happen, so just add onto existing formula
                    final Formula ltlAddition = modelChecker.generateAdditiveSafetyEncoding(k, loc, processes, stateCount, safeAllAtLocFunction);
                    final Formula addition = modelChecker.constructAdditiveBaseCase(k, ltlAddition);
                    nextKWithoutRefinementCase(kSharing, plusMinSharing, baseSolver, addition);
                    if (trackSharing)
                        sharedBaseBound.add(baseSolver.solverPlus.getLearnedConstraints().size() + baseSolver.solverMin.getLearnedConstraints().size());

                    modelChecker.clearAssumptions();
                }

                final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k).number;

                long satStartTime = System.nanoTime();

                List<Integer> assumptions = modelChecker.getAssumptions();
                assumptions.add(3);
                assumptions.add(-zVarNum);
                System.out.println("assumptions = " + assumptions);
                int[] ints = assumptions.stream().mapToInt(Integer::intValue).toArray();

                bUnknown = modelChecker.checkSatisfiability(baseSolver.solverPlus, new VecInt(ints), printTrueVars);
                long satDuration = System.nanoTime() - satStartTime;
                if (printSatTimes)
                    System.out.println("satDuration = " + satDuration / 1e9);

                if (printStats) {
                    System.out.println("baseSolver.solverPlus:");
                    printStats(baseSolver.solverPlus);
                }
                if (printLearnedConstraints) {
                    System.out.println("baseSolver.solverPlus:");
                    printLearnedConstraints(baseSolver.solverPlus);
                }
                satStartTime = System.nanoTime();
                if (!bUnknown) {
                    bNotUnknown = false;
                } else {
                    if (plusMinSharing) {
                        if (trackSharing)
                            sharedBasePlusToMinus.add(baseSolver.solverPlus.getLearnedConstraints().size());

                        assumptions = modelChecker.getAssumptions();
                        assumptions.add(-3);
                        assumptions.add(-zVarNum);
                        System.out.println("assumptions = " + assumptions);
                        ints = assumptions.stream().mapToInt(Integer::intValue).toArray();

                        bNotUnknown = modelChecker.checkSatisfiability(baseSolver.solverPlus, new VecInt(ints), printTrueVars);
                        if (printStats) {
                            System.out.println("baseSolver.solverPlus:");
                            printStats(baseSolver.solverPlus);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("baseSolver.solverPlus:");
                            printLearnedConstraints(baseSolver.solverPlus);
                        }
                    } else {
                        assumptions = modelChecker.getAssumptions();
                        assumptions.add(-3);
                        assumptions.add(-zVarNum);
                        System.out.println("assumptions = " + assumptions);
                        ints = assumptions.stream().mapToInt(Integer::intValue).toArray();

                        bNotUnknown = modelChecker.checkSatisfiability(baseSolver.solverMin, new VecInt(ints), printTrueVars);
                        if (printStats) {
                            System.out.println("baseSolver.solverMin:");
                            printStats(baseSolver.solverMin);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("baseSolver.solverMin:");
                            printLearnedConstraints(baseSolver.solverMin);
                        }
                    }
                }
                satDuration = System.nanoTime() - satStartTime;
                if (printSatTimes)
                    System.out.println("satDuration = " + satDuration / 1e9);

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
            else {
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


                    Formula step;
                    if (!stepRequiresRefinement && shouldResetStep) {
                        Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        step = modelChecker.getStepFormula(ltlEncoding, processes, stateCount, stepWithInit);

                        nextKResetFormulaCase(plusMinSharing, stepSolver, step);

                        shouldResetStep = false;
                    } else if (stepRequiresRefinement) {
                        //stepRequiresRefinement && shouldResetStep
                        Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        step = modelChecker.getStepFormula(ltlEncoding, processes, stateCount, stepWithInit);

                        refinementCase(plusMinSharing, refinementSharing, maxLengthForRefinementConstraint, stepSolver, step);
                        if (trackSharing)
                            sharedStepRefine.add(stepSolver.solverMin.getLearnedConstraints().size() + stepSolver.solverPlus.getLearnedConstraints().size());
                    } else {
                        // !stepRequiresRefinement && !shouldResetStep
                        final Formula ltlAddition = modelChecker.generateAdditiveSafetyEncoding(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        final Formula addition = modelChecker.constructAdditiveStepCase(k + 1, ltlAddition, processes, stateCount);
                        nextKWithoutRefinementCase(kSharing, plusMinSharing, stepSolver, addition);
                        if (trackSharing)
                            sharedStepBound.add(stepSolver.solverPlus.getLearnedConstraints().size() + stepSolver.solverMin.getLearnedConstraints().size());
                    }

                    final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k + 1).number;

                    long satStartTime = System.nanoTime();
                    if (plusMinSharing) {
                        sNotUnknown = modelChecker.checkSatisfiability(stepSolver.solverPlus, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("stepSolver.solverPlus:");
                            printStats(stepSolver.solverPlus);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("stepSolver.solverPlus:");
                            printLearnedConstraints(stepSolver.solverPlus);
                        }
                    } else {
                        sNotUnknown = modelChecker.checkSatisfiability(stepSolver.solverMin, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("stepSolver.solverMin:");
                            printStats(stepSolver.solverMin);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("stepSolver.solverMin:");
                            printLearnedConstraints(stepSolver.solverMin);
                        }
                    }
                    long satDuration = System.nanoTime() - satStartTime;
                    if (printSatTimes)
                        System.out.println("satDuration = " + satDuration / 1e9);

                    satStartTime = System.nanoTime();
                    if (sNotUnknown) {
                        sUnknown = true;
                    } else {
                        if (trackSharing && plusMinSharing)
                            sharedStepMinusToPlus.add(stepSolver.solverPlus.getLearnedConstraints().size());

                        sUnknown = modelChecker.checkSatisfiability(stepSolver.solverPlus, new VecInt(new int[]{3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("stepSolver.solverPlus:");
                            printStats(stepSolver.solverPlus);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("stepSolver.solverPlus:");
                            printLearnedConstraints(stepSolver.solverPlus);
                        }
                    }
                    satDuration = System.nanoTime() - satStartTime;
                    if (printSatTimes)
                        System.out.println("satDuration = " + satDuration / 1e9);

                    if (sUnknown && !sNotUnknown) {
                        predStep += 1;
                        shouldResetStep = true;
                        stepRequiresRefinement = true;
                    } else {
                        stepRequiresRefinement = false;
                    }
                } while (stepRequiresRefinement);

                if (!sUnknown) return false;

            }
        }

        System.out.println("Could not determine if error is reachable with the current max bound.");
        System.exit(1);
        return false;
    }

    private static void nextKWithoutRefinementCase(boolean kSharing, boolean plusMinSharing, SolverContainer solver, Formula addition) {
        if (plusMinSharing) {
            if (!kSharing) solver.solverPlus.clearLearntClauses();

            CNF.addClauses(solver.solverPlus, addition);
        } else {
            if (!kSharing) {
                solver.solverPlus.clearLearntClauses();
                solver.solverMin.clearLearntClauses();
            }

            CNF.addClauses(solver.solverPlus, addition);
            CNF.addClauses(solver.solverMin, addition);
        }
    }

    private static void nextKResetFormulaCase(boolean plusMinSharing, SolverContainer solver, Formula formula) {
        if (plusMinSharing) {
            solver.solverPlus = SolverFactory.newMiniLearningHeap();
            CNF.addClauses(solver.solverPlus, formula);
        } else {
            solver.solverPlus = SolverFactory.newMiniLearningHeap();
            solver.solverMin = SolverFactory.newMiniLearningHeap();
            CNF.addClauses(solver.solverPlus, formula);
            CNF.addClauses(solver.solverMin, formula);
        }
    }

    private static void refinementCase(boolean plusMinSharing, boolean refinementSharing, int maxLengthForRefinementConstraint, SolverContainer solver, Formula formula) {
        if (plusMinSharing) {
            if (refinementSharing) {
                solver.solverPlus = addLearntClauses(solver.solverPlus, maxLengthForRefinementConstraint);
            } else {
                solver.solverPlus = SolverFactory.newMiniLearningHeap();
            }
            CNF.addClauses(solver.solverPlus, formula);
        } else {
            if (refinementSharing) {
                solver.solverPlus = addLearntClauses(solver.solverPlus, maxLengthForRefinementConstraint);
                solver.solverMin = addLearntClauses(solver.solverMin, maxLengthForRefinementConstraint);
            } else {
                solver.solverPlus = SolverFactory.newMiniLearningHeap();
                solver.solverMin = SolverFactory.newMiniLearningHeap();
            }

            CNF.addClauses(solver.solverPlus, formula);
            CNF.addClauses(solver.solverMin, formula);
        }
    }

    private static Solver<DataStructureFactory> addLearntClauses(Solver<DataStructureFactory> solver, int maxLength) {
        Solver<DataStructureFactory> solver2 = SolverFactory.newMiniLearningHeap();
        IVec<Constr> learnedConstraints = solver.getLearnedConstraints();
        Iterator<Constr> iterator = learnedConstraints.iterator();

        while (iterator.hasNext()) {
            Constr next = iterator.next();
            if (next.size() <= maxLength)
                solver2.learn(next);
        }
        return solver2;
    }

    private static void printStats(Solver solver) {
        PrintWriter out = new PrintWriter(System.out);
        solver.printLearntClausesInfos(out, "Learnt clause: ");
        out.flush();
        System.out.println();

        Map stat = solver.getStat();

        stat.forEach((o, o2) -> System.out.println(o + " --- " + o2));
        System.out.println();
    }

    private static void printLearnedConstraints(Solver solver) {
        System.out.println("Learned constraints:");
        IVec learnedConstraints = solver.getLearnedConstraints();
        Iterator iterator = learnedConstraints.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            System.out.println(next);
        }
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("USAGE: inputfile.json <maxBound>");
    }

}
