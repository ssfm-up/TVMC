package za.ac.up.cs;

import org.sat4j.minisat.core.DataStructureFactory;
import org.sat4j.minisat.core.Solver;

class SolverContainer {
    public SolverContainer(Solver<DataStructureFactory> solverPlus, Solver<DataStructureFactory> solverMin) {
        this.solverPlus = solverPlus;
        this.solverMin = solverMin;
    }

    Solver<DataStructureFactory> solverPlus;
    Solver<DataStructureFactory> solverMin;

}
