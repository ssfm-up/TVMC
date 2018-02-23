package za.ac.up.cs;

import cnf.Formula;

public interface SafeLocEncodingFunction {
    Formula apply(int k, int loc, int numberOfLocs, int processes);
}
