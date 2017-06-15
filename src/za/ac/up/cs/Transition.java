package za.ac.up.cs;

import cnf.Formula;

import java.util.List;

public class Transition {
    private int source;
    private int destination;
    private List<Assignment> assignments;
    private String guard;

    public Transition() {}

    public Transition(int source, int destination, String guard, List<Assignment> assignments) {
        this.source = source;
        this.destination = destination;
        this.guard = guard;
        this.assignments = assignments;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public String getGuard() {
        return guard;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public boolean hasGuard() {
        return (guard != null);
    }

//    public class Guard {
//        private Formula a;
//        private Formula b;
//        private Formula notB;
//
//        Guard(Formula a, Formula b, Formula notB) {
//            this.a = a;
//            this.b = b;
//            this.notB = notB;
//        }
//    }
}