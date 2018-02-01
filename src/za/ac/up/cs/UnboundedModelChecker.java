package za.ac.up.cs;

import cnf.Formula;
import cnf.Var;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;
import java.util.Map;
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

    public void setCfgs(CFG cfgs) {
        threeValuedModelChecker.setCfgs(cfgs);
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

    Formula safeLoc(int k, int loc, int numberOfLocs, int processes) {
//        Formula process0 = threeValuedModelChecker.encodeLocation(0, loc, k, numberOfLocs);
//        Formula process1 = threeValuedModelChecker.encodeLocation(1, loc, k, numberOfLocs);
        ArrayList<Formula> formulas = new ArrayList<>();
        for (int i = 0; i < processes; i++) {
            formulas.add(threeValuedModelChecker.encodeLocation(i, loc, k, numberOfLocs));
        }

        return neg(and(formulas));
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

    public Map<String, Var> getVars() {
        return threeValuedModelChecker.vars;
    }
}
