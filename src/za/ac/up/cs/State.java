package za.ac.up.cs;

import java.util.List;

public class State {
    private List<Transition> transitions;

    public State() {
    }

    public State(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    @Override
    public String toString() {
        return "state";
    }
}
