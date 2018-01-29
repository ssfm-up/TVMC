package za.ac.up.cs;

import cnf.Formula;

import java.io.*;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        if (args.length > 3) {
            printUsage();
            return;
        }

        try {
            System.out.println(new java.util.Date());
            int minBound;
            int maxBound;
            if (args.length == 2) {
                minBound = 1;
                maxBound = Integer.valueOf(args[1]);
            }
            else {
                minBound = Integer.valueOf(args[1]);
                maxBound = Integer.valueOf(args[2]);
            }

            System.out.println("Max Bound: " + maxBound);

            CFG cfg = Helpers.readCfg(args[0]);
            Properties config = Helpers.loadConfigurationFile();

            System.out.println();

            long overallTime = 0;
            for (int bound = minBound; bound <= maxBound; ++bound) {

                System.out.println("Performing model checking at bound " + bound);
                int runTime = 0;
                long time = System.currentTimeMillis();
                ThreeValuedModelChecker modelChecker = new ThreeValuedModelChecker(cfg, bound, config);

                System.out.println("Encoding formula...");
                Formula formula = modelChecker.constructFormula(null);
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

                boolean notUnknownSatisfiable = false;
                if (unknownSatisfiable) {
                    System.out.print("Not Unknown Formula: ");
                    time = System.currentTimeMillis();
                    notUnknownSatisfiable = modelChecker.checkSatisfiability(notUnknownFormula);
                    timeUsed = (System.currentTimeMillis() - time);
                    runTime += timeUsed;
                    System.out.println("Finished checking not unknown formula (" + timeUsed + "ms).");
                    System.out.println();
                }

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
                else if (unknownSatisfiable) {
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

}
