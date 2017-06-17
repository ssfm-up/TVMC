package za.ac.up.cs;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dewald on 6/14/2017.
 */
public class State {
    private List<Transition> transitions;

    public State() {}

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
