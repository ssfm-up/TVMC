package za.ac.up.cs;

import java.util.List;

/**
 * Created by Dewald on 6/14/2017.
 */
public class Process {
    private List<State> states;

    public Process(List<State> states) {
        this.states = states;
    }

    @Override
    public String toString() {
        return "process";
    }
}
