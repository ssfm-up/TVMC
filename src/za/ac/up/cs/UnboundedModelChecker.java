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

    public UnboundedModelChecker(int maxbound) {
        threeValuedModelChecker = new ThreeValuedModelChecker(maxbound);
    }

    public void setCfgs(CFG cfgs) {
        threeValuedModelChecker.setCfgs(cfgs);
    }

    public Formula constructBaseCaseFormula(Formula ltlPropertyEncoding) {
        return threeValuedModelChecker.constructFormula(ltlPropertyEncoding);
    }

    public Formula constructAdditiveBaseCase(int maxBound, Formula ltlAddition) {
        return and(threeValuedModelChecker.constructAdditiveBaseCase(maxBound), ltlAddition);
    }

    public Formula constructAdditiveStepCase(int maxBound, Formula ltlAddition, int numProcesses, int numOfLocs) {
        return and(threeValuedModelChecker.constructAdditiveStepCase(maxBound, numProcesses, numOfLocs), ltlAddition);
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

    // Generates only the last part of the safety encoding
    Formula generateAdditiveSafetyEncoding(int maxBound, int loc, int processes, int numberOfLocs, SafeLocEncodingFunction f) {
        final Formula safe = f.apply(maxBound, loc, numberOfLocs, processes);
        final Formula zVar = var(threeValuedModelChecker.zVar(maxBound));

        final Formula zKMin1 = var(threeValuedModelChecker.zVar(maxBound - 1));
        return and(zKMin1, iff(safe, zVar));
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
     * Check is a satisfying assignment for a formula can be found
     *
     * @param solver        A solver to be used for the satisfiability check
     * @param constraints   The constraints that should not be learned by the solver
     * @param printTrueVars Whether to print the true variables
     * @return Is the formula satisfiable
     */
    boolean checkSatisfiability(ISolver solver, IVecInt constraints, boolean printTrueVars) {
        return threeValuedModelChecker.checkSatisfiability(solver, constraints, printTrueVars);
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

    void printFormula(Formula formula) {
        threeValuedModelChecker.printFormula(formula);
    }

    public void setMaxBound(int maxBound) {
        threeValuedModelChecker.setMaxBound(maxBound);
    }
}
