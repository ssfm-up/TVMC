package za.ac.up.cs;

import java.util.List;

public class Process {
    private List<State> states;

    public Process() {}

    public Process(List<State> states) {
        this.states = states;
    }

    public List<State> getStates() {
        return states;
    }

    public int getStateCount() {
        return states.size();
    }

    @Override
    public String toString() {
        return "process";
    }
}
