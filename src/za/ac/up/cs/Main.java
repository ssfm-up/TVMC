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
            System.out.println(new java.util.Date());
            int maxBound = Integer.valueOf(args[1]);
            System.out.println("Max Bound: " + maxBound);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            File file = new File(args[0]);
            CFG cfg = objectMapper.readValue(file, CFG.class);

            Properties config = loadConfigurationFile();
            System.out.println();
            long overallTime = 0;
            for (int bound = 0; bound <= maxBound; ++bound) {

                System.out.println("Performing model checking at bound " + bound);
                int runTime = 0;
                long time = System.currentTimeMillis();
                ThreeValuedModelChecker modelChecker = new ThreeValuedModelChecker(cfg, bound, config);

                System.out.println("Encoding formula...");
                Formula ltlEncoding = null;
                Formula formula = modelChecker.constructFormula(ltlEncoding);
                Formula unknownFormula = modelChecker.getUnknownFormula(formula);
                Formula notUnknownFormula = modelChecker.getNotUnknownFormula(formula);
                long timeUsed = (System.currentTimeMillis() - time);
                runTime += timeUsed;
                System.out.println("Finished encoding formula (" + timeUsed + "ms).");

                System.out.println();
                System.out.println("Checking satisfiability...");
                System.out.print("Unknown Formula: ");
                time = System.currentTimeMillis();
                boolean unknownSatisfiable = modelChecker.checkSatisfiability(unknownFormula);
                timeUsed = (System.currentTimeMillis() - time);
                runTime += timeUsed;
                System.out.println("Finished checking unknown formula (" + timeUsed + "ms).");
                System.out.println();

                System.out.print("Not Unknown Formula: ");
                time = System.currentTimeMillis();
                boolean notUnknownSatisfiable = modelChecker.checkSatisfiability(notUnknownFormula);
                timeUsed = (System.currentTimeMillis() - time);
                runTime += timeUsed;
                System.out.println("Finished checking not unknown formula (" + timeUsed + "ms).");
                System.out.println();

                System.out.println("Total time for bound " + bound + ": " + runTime + "ms");
                overallTime += runTime;
                System.out.println("\n=============================================================================\n");
                if (unknownSatisfiable && !notUnknownSatisfiable) {
                    System.out.println();
                    modelChecker.printVars();
                    System.out.println();
                    System.out.println("Overall Time: " + overallTime + " ms");
                    System.out.println("Refinement necessary. Exiting...");
                    return;
                }
                else if (unknownSatisfiable && notUnknownSatisfiable) {
                    System.out.println();
                    modelChecker.printVars();
                    System.out.println();
                    System.out.println("Overall Time: " + overallTime + " ms");
                    System.out.println("Error found. Exiting...");
                    return;
                }
            }
            System.out.println("Overall Time: " + overallTime + " ms");
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
