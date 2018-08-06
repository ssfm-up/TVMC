package za.ac.up.cs;

import cnf.Formula;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static cnf.CNF.*;

public class Process {
    private List<State> states;

    public Process() {
    }

    public Process(List<State> states) {
        this.states = states;
    }

    Formula getEncoding(ThreeValuedModelChecker mc, int process, int bound, int r, LogicParser parser) {
        List<Formula> formulas = new LinkedList<>();
        for (State state : getStates()) {
            for (Transition transition : state.getTransitions()) {
                List<Formula> currentTransEncoding = new LinkedList<>();

                int source = transition.getSource();
                int destination = transition.getDestination();
                Formula locEncoding = and(mc.encodeLocation(process, source, bound, getStateCount()), mc.encodeLocation(process, destination, r, getStateCount()));
                currentTransEncoding.add(locEncoding);

                // TODO: Check if this condition and loop below it should be done for each transition
                if (mc.checkFairness) {
                    // /\ progress[i] [k+1]
                    currentTransEncoding.add(var(mc.progress[process][r]));
                }

                for (int i = 0; i < mc.cfgs.getNumberOfProcesses(); i++) {
                    if (i != process) {
                        Formula idlingEncoding = idleTransitionEncoding(mc, i, bound, r, mc.cfgs.getProcess(i).getStateCount());
                        currentTransEncoding.add(idlingEncoding);

                        if (mc.checkFairness) {
                            // /\ not progress[j][k+1]
                            currentTransEncoding.add(neg(var(mc.progress[i][r])));
                        }
                    }
                }

                if (transition.hasGuard()) {
                    currentTransEncoding.add(transition.getGuardEncoding(mc, parser, process));
                }

                currentTransEncoding.addAll(transition.getAssignmentEncodings(mc, parser, bound, r));

                formulas.add(and(currentTransEncoding));
            }
        }
        return or(formulas);
    }

    private Formula idleTransitionEncoding(ThreeValuedModelChecker mc, int process, int bound, int r, int stateCount) {
        List<Formula> formulas = new ArrayList<>();

        for (int i = 0; i < stateCount; i++) {
            Formula a = mc.encodeLocation(process, i, bound, stateCount);
            Formula b = mc.encodeLocation(process, i, r, stateCount);
            formulas.add(iff(a, b));
        }

        return and(formulas);
    }

    public List<State> getStates() {
        return states;
    }

    public void deleteState(int s) {
        states.remove(s);
    }

    void updateTransitions() {
        for (int i = 0; i < states.size(); i++) {
            states.get(i).rename(i);
        }
    }

    public int getStateCount() {
        return states.size();
    }

    @Override
    public String toString() {
        return "process";
    }
}
