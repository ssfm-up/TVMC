package za.ac.up.cs;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dewald on 6/14/2017.
 */
public class State {
    private List<Transition> transitions;

    public State(List<Transition> transitions) {
        this.transitions = transitions;
    }

    @Override
    public String toString() {
        return "state";
    }
}
