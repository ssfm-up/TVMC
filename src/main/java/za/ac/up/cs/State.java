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

    public void rename(int i) {
        for (Transition t: transitions) {
            int destination;
            if (t.getDestination() == 0) {
                destination = 0;
            }
            else {
                destination = t.getDestination() - 2;
            }
            t.rename(i, destination);
        }
    }

    @Override
    public String toString() {
        return "state";
    }
}
