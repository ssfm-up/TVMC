package za.ac.up.cs;

import cnf.Formula;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.core.Solver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static cnf.CNF.and;
import static cnf.CNF.cnf;
import static cnf.CNF.neg;

public class UnboundedMain {

    public static void main(String[] args) {
        if (args.length > 3) {
            printUsage();
            return;
        }

        try {
            System.out.println(new Date());
            int minBound;
            int maxBound;
            if (args.length == 2) {
                minBound = 1;
                maxBound = Integer.valueOf(args[1]);
            } else {
                minBound = Integer.valueOf(args[1]);
                maxBound = Integer.valueOf(args[2]);
            }

            System.out.println("Max Bound: " + maxBound);

            CFG cfg = Helpers.readCfg(args[0]);
            Properties config = Helpers.loadConfigurationFile();

            System.out.println();

            UnboundedModelChecker modelChecker = new UnboundedModelChecker(cfg, maxBound, config);
            Formula baseCase = modelChecker.getBaseCaseFormula();
            System.out.println("baseCase = " + baseCase);

            Solver solver = SolverFactory.newMiniLearningHeap();
//            boolean b = modelChecker.checkSatisfiability(and(baseCase, neg(UnboundedModelChecker.UNKNOWN)), solver, null);
            boolean b = modelChecker.checkSatisfiability(baseCase, solver, new VecInt(new int[]{-3}));
            modelChecker.printVars();
            System.out.println("Is satisfiable? = " + b);

            PrintWriter out = new PrintWriter(System.out);

//            Vec<VecInt> assump1 = CNF.getClauses(ThreeValuedModelChecker.UNKNOWN);

            solver.printLearntClausesInfos(out, "Learnt clause: ");
            out.flush();
            System.out.println();

            IVecInt outLearnt = solver.getOutLearnt();
            IVec learnedConstraints = solver.getLearnedConstraints();
//                IteratorInt iterator = outLearnt.iterator();
            Iterator iterator = learnedConstraints.iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();
                System.out.println("Learned constraint: " + next);
            }
            Map stat = solver.getStat();

            stat.forEach((o, o2) -> System.out.println(o + " --- " + o2));


//            solver.addClause()
//        solver.addAllClauses(clauses); //add a formula consisting of clauses to the solver
//        solver.addClause(literals);    //add a clause consisting of literals to the solver
//        solver.isSatisfiable(assumps); // check satisfiability under assumptions e.g. 3 or -3 if 3 represents 'unknown'


        } catch (IOException e) {
            System.out.println("IOException");
        } catch (NumberFormatException e) {
            System.out.println("Number format exception");
        }


    }

    private static void printUsage() {
        System.out.println("USAGE: inputfile.json <maxBound>");
    }

}