package za.ac.up.cs;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dewald on 6/14/2017.
 */
public class CFG implements Serializable {
    public Map<String, Integer> predicates;
    private List<Process> processes;

    public CFG(Map<String, Integer> predicates, List<Process> processes) {
        this.predicates = predicates;
        this.processes = processes;
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

//        JsonFactory factory = new JsonFactory();

//        JsonGenerator generator = null;
//        try {
//            generator = factory.createGenerator(System.out);
//            generator.writeStartObject();
//            //generator.writeStringField("predicates", "Mercedes");
//            generator.writeEmbeddedObject(predicates);
//            //generator.writeNumberField("doors", 5);
//            generator.writeEndObject();
//
//            generator.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        return json;
    }
}
