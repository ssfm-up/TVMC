package za.ac.up.cs;

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

    public Assignment() {}

    public Assignment(Integer predicate, String RHS) {
        this.predicate = predicate;
        this.RHS = RHS;
    }

    public int getPredicate() {
        return predicate;
    }

    public String getRHS() {
        return RHS;
    }
}