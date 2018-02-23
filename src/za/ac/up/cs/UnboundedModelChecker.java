package za.ac.up.cs;

import cnf.Formula;
import cnf.Var;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static cnf.CNF.*;

public class UnboundedModelChecker {
    public static final Formula UNKNOWN = ThreeValuedModelChecker.UNKNOWN;
    final ThreeValuedModelChecker threeValuedModelChecker;

    public UnboundedModelChecker(CFG cfgs, int maxBound, Properties config) {
        threeValuedModelChecker = new ThreeValuedModelChecker(cfgs, maxBound, config);
    }

    public void setCfgs(CFG cfgs) {
        threeValuedModelChecker.setCfgs(cfgs);
    }

    public Formula getBaseCaseFormula(Formula ltlPropertyEncoding) {
        return threeValuedModelChecker.constructFormula(ltlPropertyEncoding);
    }

    Formula getStepFormula(Formula ltlPropertyEncoding, int numProcesses, int numLocs) {
        return threeValuedModelChecker.constructStepFormula(ltlPropertyEncoding, numProcesses, numLocs);
    }

    Formula safeAnyPairAtLoc(int k, int loc, int numberOfLocs, int processes) {
        ArrayList<Formula> formulas = new ArrayList<>();
        for (int i = 0; i < processes - 1; i++) {
            for (int j = i + 1; j < processes; j++) {
                Formula locI = threeValuedModelChecker.encodeLocation(i, loc, k, numberOfLocs);
                Formula locJ = threeValuedModelChecker.encodeLocation(j, loc, k, numberOfLocs);
                formulas.add(neg(and(locI, locJ)));
            }
        }

        return and(formulas);
    }

    private Formula unsafeAllAtLoc(int k, int loc, int numberOfLocs, int processes) {
        ArrayList<Formula> formulas = new ArrayList<>();
        for (int i = 0; i < processes; i++) {
            Formula locI = threeValuedModelChecker.encodeLocation(i, loc, k, numberOfLocs);
            formulas.add(locI);
        }

        return and(formulas);
    }

    Formula safeAllAtLoc(int k, int loc, int numberOfLocs, int processes) {
        return neg(unsafeAllAtLoc(k, loc, numberOfLocs, processes));
    }

    Formula generateSafetyEncodingFormula(int maxBound, int loc, int processes, int numberOfLocs, SafeLocEncodingFunction f) {
        List<Formula> safetyFormulas = new ArrayList<>();
        for (int k = 0; k <= maxBound - 1; k++) {
            final Formula safe = f.apply(k, loc, numberOfLocs, processes);
            final Formula zVar = var(threeValuedModelChecker.zVar(k));
            safetyFormulas.add(and(iff(safe, zVar), zVar));
        }
        final Formula safe = f.apply(maxBound, loc, numberOfLocs, processes);
        final Formula zVar = var(threeValuedModelChecker.zVar(maxBound));
        safetyFormulas.add(iff(safe, zVar));

        return and(safetyFormulas);
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
