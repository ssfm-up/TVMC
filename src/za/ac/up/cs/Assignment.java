package za.ac.up.cs;

import cnf.Formula;

import java.util.Map;

public class Assignment {
    //private Formula a;
    //private Formula b;
    private String RHS;
    private int predicate;

//    public Assignment(Integer predicate, String RHS, LogicParser parser, Map<String, Integer> predMap) {
//        //ParserHelper parserHelper = new ParserHelper(parser, RHS, predMap).invoke();
//        //this.a = parserHelper.getA();
//        //this.b = parserHelper.getB();
//        this.predicate = predicate;
//    }

    public Assignment(Integer predicate, String RHS) {
        this.predicate = predicate;
        this.RHS = RHS;
    }
}