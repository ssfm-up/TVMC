package za.ac.up.cs;

import cnf.Formula;
import cnf.TseitinVisitor;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.core.Solver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.Properties;
import java.util.Set;

public class UnboundedModelChecker {
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

    public Formula getBaseCaseFormula() {
        return threeValuedModelChecker.constructFormula(null);
    }

}
