package za.ac.up.cs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Helpers {
    private final static String CONFIG_FILE = "config.properties";

    static CFG readCfg(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        File file = new File(filename);
        CFG cfg = objectMapper.readValue(file, CFG.class);
        cfg.prune();
        return cfg;
    }

    /**
     * Loads the properties file, config.properties, if it exists otherwise writes one with default values and returns it
     *
     * @return A Properties object containing the program configuration
     */
    static Properties loadConfigurationFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE);
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return prop;
    }
}
