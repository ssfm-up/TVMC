package za.ac.up.cs;

import cnf.Formula;

import java.util.List;
import java.util.Map;

import static cnf.CNF.and;
import static cnf.CNF.neg;

/**
 * Created by Dewald on 6/14/2017.
 */
public class Transition {
    private int source;
    private int destination;
    private List<Assignment> assignments;
    private String guard;

    public Transition(int source, int destination, String guard, List<Assignment> assignments) {
        this.source = source;
        this.destination = destination;
        this.guard = guard;
        this.assignments = assignments;
    }

    public class Guard {
        private Formula a;
        private Formula b;
        private Formula notB;

        Guard(Formula a, Formula b, Formula notB) {
            this.a = a;
            this.b = b;
            this.notB = notB;
        }
    }


}
