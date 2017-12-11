package za.ac.up.cs;

import cnf.Formula;
import cnf.TseitinVisitor;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.core.Solver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.Properties;
import java.util.Set;

public class UnboundedModelChecker {
    private final ThreeValuedModelChecker threeValuedModelChecker;
    public static final Formula UNKNOWN = ThreeValuedModelChecker.UNKNOWN;

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

    public Formula getBaseCaseFormula() {
        return threeValuedModelChecker.constructFormula(null);
    }

    /**
     * Check is a satisfying assignment for a formula can be found
     *
     * @param formula The formula to be checked
     * @param solver A solver to be used for the satisfiability check
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
