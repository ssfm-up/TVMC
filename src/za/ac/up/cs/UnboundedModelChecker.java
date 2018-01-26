package za.ac.up.cs;

import cnf.Formula;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

import java.util.Properties;

import static cnf.CNF.and;
import static cnf.CNF.neg;
import static cnf.CNF.or;

public class UnboundedModelChecker {
    public static final Formula UNKNOWN = ThreeValuedModelChecker.UNKNOWN;
    private final ThreeValuedModelChecker threeValuedModelChecker;

    public UnboundedModelChecker(CFG cfgs, int maxBound, Properties config) {
        threeValuedModelChecker = new ThreeValuedModelChecker(cfgs, maxBound, config);
    }

//    public static Set<Var> satisfiable(Formula f) throws TimeoutException {
//        ISolver solver = SolverFactory.newMiniLearningHeap();
//        solver = addClauses(solver, f);
//        if (solver == null) {
//            return null;
//        }
//
//        if (solver.isSatisfiable()) {
//            int[] model = solver.model();
//            Set<Var> trueVars = new HashSet<>();
//            for (Integer y : model) {
//                if (y > 0) {
//                    trueVars.add(new Var(y));
//                }
//            }
//            return trueVars;
//        } else {
//            return null;
//        }
//    }

    public Formula getBaseCaseFormula(Formula ltlPropertyEncoding) {
        return threeValuedModelChecker.constructFormula(ltlPropertyEncoding);
    }

    Formula safeLoc(int k, int loc) {
        Formula process0 = threeValuedModelChecker.encodeLocation(0, loc, k, 4);
        Formula process1 = threeValuedModelChecker.encodeLocation(1, loc, k, 4);
        return neg(and(process0, process1));
    }


    /**
     * Check is a satisfying assignment for a formula can be found
     *
     * @param formula     The formula to be checked
     * @param solver      A solver to be used for the satisfiability check
     * @param constraints The constraints that should not be learned by the solver
     * @return Is the formula satisfiable
     */
    boolean checkSatisfiability(Formula formula, ISolver solver, IVecInt constraints) {
        return threeValuedModelChecker.checkSatisfiability(formula, solver, constraints);
    }

    /**
     * Print a table showing the mapping from variables names to aliases in solvers' naming conventions
     */
    void printVars() {
        threeValuedModelChecker.printVars();
    }

}
