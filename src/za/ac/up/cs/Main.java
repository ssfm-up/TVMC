package za.ac.up.cs;

import cnf.Formula;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Main {

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

            int overallTime = 0;
            long time = System.currentTimeMillis();
            ThreeValuedModelChecker modelChecker = new ThreeValuedModelChecker(cfg, Integer.valueOf(args[1]));

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

}
