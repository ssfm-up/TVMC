package za.ac.up.cs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFG implements Serializable {
    private Map<String, Integer> predicates;
    private List<Process> processes;

    public CFG() {
    }

    public CFG(Map<String, Integer> predicates, List<Process> processes) {
        this.predicates = predicates;
        this.processes = processes;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public Map<String, Integer> getPredicates() {
        return predicates;
    }

    public Process getProcess(int p) {
        if (p <= processes.size() && p >= 0) {
            return processes.get(p);
        }
        // TODO: Throw an exception instead?
        throw new IndexOutOfBoundsException("Process index " + p + " is out of bounds!");
    }

    int getPredicate(String key) {
        return predicates.get(key);
    }

    public String getPredicateString(int value) {
        for (Map.Entry<String, Integer> entry : predicates.entrySet()) {
            if (value == entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void prune() {
        for (Process p : processes) {
            p.deleteState(1);
            p.deleteState(0);
            p.updateTransitions();
        }
    }

    public int getNumberOfProcesses() {
        return processes.size();
    }

    int getNumberOfPredicates() {
        return predicates.size();
    }

    @Override
    public String toString() {
        ObjectMapper obj = new ObjectMapper();
        obj.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String json = "";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("predicates", predicates);
            map.put("processes", processes);
            json = obj.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
