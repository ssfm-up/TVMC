package za.ac.up.cs;

import cnf.Formula;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Properties;

public class Main {

    final static String CONFIG_FILE = "config.properties";

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            File file = new File(args[0]);
            CFG cfg = objectMapper.readValue(file, CFG.class);

            Properties config = loadConfigurationFile();

            int overallTime = 0;
            long time = System.currentTimeMillis();
            ThreeValuedModelChecker modelChecker = new ThreeValuedModelChecker(cfg, Integer.valueOf(args[1]), config);

            System.out.println("Encoding formula...");
            Formula ltlEncoding = null;
            Formula formula = modelChecker.constructFormula(ltlEncoding);
            Formula unknownFormula = modelChecker.getUnknownFormula(formula);
            Formula notUnknownFormula = modelChecker.getNotUnknownFormula(formula);
            long timeUsed = (System.currentTimeMillis() - time);
            overallTime += timeUsed;
            System.out.println("Finished encoding formula (" + timeUsed + "ms).");

            System.out.println();
            System.out.println("Checking satisfiability...");
            System.out.print("Unknown Formula: ");
            time = System.currentTimeMillis();
            modelChecker.checkSatisfiability(unknownFormula);
            timeUsed = (System.currentTimeMillis() - time);
            overallTime += timeUsed;
            System.out.println("Finished checking unknown formula (" + timeUsed + "ms).");
            System.out.println();

            System.out.print("Not Unknown Formula: ");
            time = System.currentTimeMillis();
            modelChecker.checkSatisfiability(notUnknownFormula);
            timeUsed = (System.currentTimeMillis() - time);
            overallTime += timeUsed;
            System.out.println("Finished checking not unknown formula (" + timeUsed + "ms).");
            System.out.println();

            System.out.println("Total time: " + overallTime + "ms");

            modelChecker.printVars();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("USAGE: inputfile.json <maxBound>");
    }

    /**
     * Loads the properties file, config.properties, if it exists otherwise writes one with default values and returns it
     *
     * @return A Properties object containing the program configuration
     */
    private static Properties loadConfigurationFile() {
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
