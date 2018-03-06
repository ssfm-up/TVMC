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

    public static void main(String[] args) throws IOException {
//        iterateLocations();
        final long startTime = System.nanoTime();
        boolean result = start(300, 2, 2);
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

        Solver<DataStructureFactory> baseSolverP = SolverFactory.newMiniLearningHeap();
        Solver<DataStructureFactory> baseSolverM = SolverFactory.newMiniLearningHeap();
        Solver<DataStructureFactory> stepSolverP = SolverFactory.newMiniLearningHeap();
        Solver<DataStructureFactory> stepSolverM = SolverFactory.newMiniLearningHeap();
        boolean shouldResetStep = true;
        boolean shouldResetBase = true;
        final boolean printTrueVars = false;
        final boolean printStats = true;
        final boolean printLearnedConstraints = false;
        final boolean printSatTimes = true;
        final boolean kSharing = true;
        final boolean plusMinSharing = true;
        final boolean refinementSharing = true;
        final boolean stepWithInit = true;


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

                    if (plusMinSharing) {
                        baseSolverP = SolverFactory.newMiniLearningHeap();
                        CNF.addClauses(baseSolverP, baseCase);
                    } else {
                        baseSolverP = SolverFactory.newMiniLearningHeap();
                        baseSolverM = SolverFactory.newMiniLearningHeap();
                        CNF.addClauses(baseSolverP, baseCase);
                        CNF.addClauses(baseSolverM, baseCase);
                    }

                    shouldResetBase = false;
                } else if (baseRequiresRefinement) {
                    // baseRequiresRefinement && shouldResetBase
                    // Predicate refinement case
                    Formula ltlEncodingBase = modelChecker.generateSafetyEncodingFormula(k, loc, processes, stateCount, safeAllAtLocFunction);
                    baseCase = modelChecker.constructBaseCaseFormula(ltlEncodingBase);

                    if (plusMinSharing) {
                        if (refinementSharing) {
                            baseSolverP = addLearntClauses(baseSolverP);
                        } else {
                            baseSolverP = SolverFactory.newMiniLearningHeap();
                        }
                        CNF.addClauses(baseSolverP, baseCase);
                    } else {
                        if (refinementSharing) {
                            baseSolverP = addLearntClauses(baseSolverP);
                            baseSolverM = addLearntClauses(baseSolverM);
                        } else {
                            baseSolverP = SolverFactory.newMiniLearningHeap();
                            baseSolverM = SolverFactory.newMiniLearningHeap();
                        }

                        CNF.addClauses(baseSolverP, baseCase);
                        CNF.addClauses(baseSolverM, baseCase);
                    }
                } else {
                    // !baseRequiresRefinement && !shouldResetBase
                    // Next k case and predicate refinement did not happen, so just add onto existing formula
                    final Formula ltlAddition = modelChecker.generateAdditiveSafetyEncoding(k, loc, processes, stateCount, safeAllAtLocFunction);
                    final Formula addition = modelChecker.constructAdditiveBaseCase(k, ltlAddition);
                    if (plusMinSharing) {
                        if (!kSharing) baseSolverP.clearLearntClauses();

                        CNF.addClauses(baseSolverP, addition);
                    } else {
                        if (!kSharing) {
                            baseSolverP.clearLearntClauses();
                            baseSolverM.clearLearntClauses();
                        }

                        CNF.addClauses(baseSolverP, addition);
                        CNF.addClauses(baseSolverM, addition);
                    }
                }

                final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k).number;

                long satStartTime = System.nanoTime();
                bUnknown = modelChecker.checkSatisfiability(baseSolverP, new VecInt(new int[]{3, -zVarNum}), printTrueVars);
                long satDuration = System.nanoTime() - satStartTime;
                if (printSatTimes)
                    System.out.println("satDuration = " + satDuration / 1e9);

                if (printStats) {
                    System.out.println("baseSolverP:");
                    printStats(baseSolverP);
                }
                if (printLearnedConstraints) {
                    System.out.println("baseSolverP:");
                    printLearnedConstraints(baseSolverP);
                }
                satStartTime = System.nanoTime();
                if (!bUnknown) {
                    bNotUnknown = false;
                } else {
                    if (plusMinSharing) {
                        bNotUnknown = modelChecker.checkSatisfiability(baseSolverP, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("baseSolverP:");
                            printStats(baseSolverP);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("baseSolverP:");
                            printLearnedConstraints(baseSolverP);
                        }
                    } else {
                        bNotUnknown = modelChecker.checkSatisfiability(baseSolverM, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("baseSolverM:");
                            printStats(baseSolverM);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("baseSolverM:");
                            printLearnedConstraints(baseSolverM);
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

                        if (plusMinSharing) {
                            stepSolverP = SolverFactory.newMiniLearningHeap();
                            CNF.addClauses(stepSolverP, step);
                        } else {
                            stepSolverP = SolverFactory.newMiniLearningHeap();
                            stepSolverM = SolverFactory.newMiniLearningHeap();
                            CNF.addClauses(stepSolverP, step);
                            CNF.addClauses(stepSolverM, step);
                        }

                        shouldResetStep = false;
                    } else if (stepRequiresRefinement) {
                        //stepRequiresRefinement && shouldResetStep
                        Formula ltlEncoding = modelChecker.generateSafetyEncodingFormula(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        step = modelChecker.getStepFormula(ltlEncoding, processes, stateCount, stepWithInit);

                        if (plusMinSharing) {
                            if (refinementSharing) {
                                stepSolverP = addLearntClauses(stepSolverP);
                            } else {
                                stepSolverP = SolverFactory.newMiniLearningHeap();
                            }
                            CNF.addClauses(stepSolverP, step);
                        } else {
                            if (refinementSharing) {
                                stepSolverP = addLearntClauses(stepSolverP);
                                stepSolverM = addLearntClauses(stepSolverM);
                            } else {
                                stepSolverP = SolverFactory.newMiniLearningHeap();
                                stepSolverM = SolverFactory.newMiniLearningHeap();
                            }

                            CNF.addClauses(stepSolverP, step);
                            CNF.addClauses(stepSolverM, step);
                        }
                    } else {
                        // !stepRequiresRefinement && !shouldResetStep
                        final Formula ltlAddition = modelChecker.generateAdditiveSafetyEncoding(k + 1, loc, processes, stateCount, safeAllAtLocFunction);
                        final Formula addition = modelChecker.constructAdditiveStepCase(k + 1, ltlAddition, processes, stateCount);
                        if (plusMinSharing) {
                            if (!kSharing) stepSolverP.clearLearntClauses();

                            CNF.addClauses(stepSolverP, addition);
                        } else {
                            if (!kSharing) {
                                stepSolverP.clearLearntClauses();
                                stepSolverM.clearLearntClauses();
                            }

                            CNF.addClauses(stepSolverP, addition);
                            CNF.addClauses(stepSolverM, addition);
                        }
                    }

                    final int zVarNum = modelChecker.threeValuedModelChecker.zVar(k + 1).number;

                    long satStartTime = System.nanoTime();
                    if (plusMinSharing) {
                        sNotUnknown = modelChecker.checkSatisfiability(stepSolverP, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("stepSolverP:");
                            printStats(stepSolverP);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("stepSolverP:");
                            printLearnedConstraints(stepSolverP);
                        }
                    } else {
                        sNotUnknown = modelChecker.checkSatisfiability(stepSolverM, new VecInt(new int[]{-3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("stepSolverM:");
                            printStats(stepSolverM);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("stepSolverM:");
                            printLearnedConstraints(stepSolverM);
                        }
                    }
                    long satDuration = System.nanoTime() - satStartTime;
                    if (printSatTimes)
                        System.out.println("satDuration = " + satDuration / 1e9);

                    satStartTime = System.nanoTime();
                    if (sNotUnknown) {
                        sUnknown = true;
                    } else {
                        sUnknown = modelChecker.checkSatisfiability(stepSolverP, new VecInt(new int[]{3, -zVarNum}), printTrueVars);
                        if (printStats) {
                            System.out.println("stepSolverP:");
                            printStats(stepSolverP);
                        }
                        if (printLearnedConstraints) {
                            System.out.println("stepSolverP:");
                            printLearnedConstraints(stepSolverP);
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
