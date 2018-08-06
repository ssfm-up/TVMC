package za.ac.up.cs;


import java.util.ArrayList;
import java.util.List;

public class Path {
    CFG cfgs;
    int bound;
    List<Step> steps;
    List<String> progressSteps;

    Path(CFG cfgs, int bound) {
        this.cfgs = cfgs;
        this.bound = bound;
        progressSteps = new ArrayList<>();
        steps = new ArrayList<>();
        for (int i = 0; i <= bound; ++i) {
            steps.add(new Step(cfgs, i));
        }
        // Add the initial state
//        for (int i = 0; i < cfgs.getProcess(0).getStateCount(); ++i) {
//            steps.get(0).addLocation("l_0_0_0");encodeLocation(i, 0, 0, processes.get(i).getStateCount()));
//            if (checkFairness) initialProgressValues.add(neg(var(progress[i][0])));
//        }
    }

    void addLocation(String location) {
        // l_proc_loc_bound
        String[] loc = location.split("_");
        steps.get(Integer.valueOf(loc[3])).addLocation(location);
    }

    void addPredicate(String predicate) {
        // p_pred_bound_known
        String[] pred = predicate.split("_");
        steps.get(Integer.valueOf(pred[2])).addPredicate(predicate);
    }

    void addProgressStep(String progress) {
        // progress_proc_bound
        progressSteps.add(progress);
    }

    int getProgressAtBound(int bound) {
        for (String progress : progressSteps) {
            String[] prog = progress.split("_");
            if (Integer.valueOf(prog[2]) == bound + 1) return Integer.valueOf(prog[1]);
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Step step : steps) {
            sb.append(step);
            int proc = getProgressAtBound(step.getBound());
            if (proc != -1) {
                sb.append("\t--- ");
                sb.append(proc);
                sb.append(" --->\n");
            }
        }
        return sb.toString();
    }
}
