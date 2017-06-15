package za.ac.up.cs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFG implements Serializable {
    private Map<String, Integer> predicates;
    private List<Process> processes;

    public CFG() {}

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
        return null;
    }

    public int getNumberOfProcesses() {
        return processes.size();
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
