package za.ac.up.cs;

import java.util.ArrayList;

public class Step {
    CFG cfgs;
    int bound;
    ArrayList<String> predicates;
    ArrayList<String> locations;

    Step(CFG cfgs, int bound) {
        this.cfgs = cfgs;
        locations = new ArrayList<>();
        predicates = new ArrayList<>();
        this.bound = bound;
    }

    void addLocation(String location) {
        locations.add(location);
    }

    void addPredicate(String predicate) {
        predicates.add(predicate);
    }

    int getBound() {
        return bound;
    }

    int getLocationOfProcess(int p) {
        int numOfBinaryDigits = (int) Math.ceil(Math.log(cfgs.getProcess(p).getStateCount()) / Math.log(2.0));
        char[] position = new char[numOfBinaryDigits];
        for (int i = 0; i < numOfBinaryDigits; ++i) position[i] = '0';
        for (String location : locations) {
            String[] loc = location.split("_");
            if (Integer.valueOf(loc[1]) == p) position[Integer.valueOf(loc[2])] = '1';
        }
        return Integer.parseInt(String.valueOf(position), 2);
    }

    char getValueOfPred(int p) {
        boolean known = false;
        boolean unknown = false;
        for (String predicate : predicates) {
            String[] pred = predicate.split("_");
            if (Integer.valueOf(pred[1]) == p) {
                if (pred[3].equals("b")) known = true;
                else if (pred[3].equals("u")) unknown = true;
            }
        }

        if (known && !unknown) return 't';
        else if (!known && !unknown) return 'f';
        else return 'u';
    }

    @Override
    public String toString() {
        int numberOfProcesses = cfgs.getNumberOfProcesses();
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        for (int i = 0; i < numberOfProcesses; i++) {
            sb.append(getLocationOfProcess(i));
            sb.append(", ");
        }
        int numberOfPredicates = cfgs.getNumberOfPredicates();
        for (int x = 0; x < numberOfPredicates; ++x) {
            sb.append(cfgs.getPredicateString(x));
            sb.append("=");
            sb.append(getValueOfPred(x));
            if (x != numberOfPredicates - 1) sb.append(", ");
        }
        sb.append(">");
        return sb.toString();
    }
}
