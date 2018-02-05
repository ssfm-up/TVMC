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

import static cnf.CNF.and;
import static cnf.CNF.neg;

public class UnboundedMain {

    //examples/new/5Philosophers4P.json examples/new/5Philosophers5P.json 5
    //examples/2Philosophers_1P/2Philosophers_1P.json examples/2Philosophers_2P/2Philosophers_2P.json 5
    public static void main(String[] args) throws IOException {
        if (args.length > 3) {
            printUsage();
            return;
        }
        int loc = 2;
//        int processes = 5;
        int processes = 2;
        int numberOfLocs = 4;

        try {
            System.out.println(new Date());
            int maxBound = Integer.parseInt(args[2]);

            System.out.println("Max Bound: " + maxBound);

            CFG cfg1 = Helpers.readCfg(args[0]);
            CFG cfg2 = Helpers.readCfg(args[1]);

            Properties config = Helpers.loadConfigurationFile();
            System.out.println();
            UnboundedModelChecker modelChecker = new UnboundedModelChecker(cfg1, maxBound, config);
            Solver<DataStructureFactory> solver = SolverFactory.newMiniLearningHeap();

            long startTime = System.nanoTime();
            System.out.println("===========" + args[0] + "===========");

            checkSafety(maxBound, modelChecker, solver, loc, processes, numberOfLocs);
            System.out.println();
            System.out.println("===========" + args[1] + "===========");
            modelChecker.setCfgs(cfg2);

            Solver<DataStructureFactory> solver2 = addLearntClauses(solver);

            checkSafety(maxBound, modelChecker, solver2, loc, processes, numberOfLocs);

            long endTime = System.nanoTime();
            System.out.println("Time elapsed: " + (endTime - startTime) / 1e9 + " seconds");
//        solver.addAllClauses(clauses); //add a formula consisting of clauses to the solver
//        solver.addClause(literals);    //add a clause consisting of literals to the solver
//        solver.isSatisfiable(assumps); // check satisfiability under assumptions e.g. 3 or -3 if 3 represents 'unknown'


        } catch (NumberFormatException e) {
            System.out.println("Number format exception: " + e);
        }


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
        System.out.println("baseCase = " + baseCase);
        System.out.println();

        System.out.println("==== UNKNOWN FORMULA ====");
        ArrayList<Integer> unknownAssumptionList = new ArrayList<>();
        unknownAssumptionList.add(3);
        int[] unknownAssumptions = unknownAssumptionList.stream().mapToInt(x -> x).toArray();
        boolean bUnknown = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(unknownAssumptions));
        modelChecker.printVars();
        System.out.println("Is satisfiable? = " + bUnknown);

        printStats(solver);

        System.out.println();
        System.out.println("==== NOT UNKNOWN FORMULA ====");
        ArrayList<Integer> notUnknownAssumptionList = new ArrayList<>();
        notUnknownAssumptionList.add(-3);
        int[] notUnknownAssumptions = notUnknownAssumptionList.stream().mapToInt(x -> x).toArray();

        boolean bNotUnknown = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(notUnknownAssumptions));
        modelChecker.printVars();
        System.out.println("Is satisfiable? = " + bNotUnknown);

        printStats(solver);

        System.out.println();

        System.out.println("Unknown formula satisfiable: " + bUnknown);
        System.out.println("Not unknown formula satisfiable: " + bNotUnknown);

        Formula step = modelChecker.getStepFormula(ltlEncoding, processes, numberOfLocs);
        System.out.println("step = " + step);
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
