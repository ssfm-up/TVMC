package za.ac.up.cs;

import cnf.Formula;

import java.util.Map;

/**
 * Parses a choice expression and exposes the left part of the encoding with getA and the right with getB.
 * Use the invoke method, e.g. ParserHelper parserHelper = new ParserHelper(parser, str).invoke();
 */
public class ParserHelper {
    private LogicParser parser;
    private String s;
    private Formula a;
    private Formula b;
    private Map<String, Integer> predMap;

    public ParserHelper(LogicParser parser, String s, Map<String, Integer> predMap) {
        this.parser = parser;
        this.s = s;
        this.predMap = predMap;
    }

    public Formula getA() {
        return a;
    }

    public Formula getB() {
        return b;
    }

    public ParserHelper invoke() {
        String[] split = s.split(",");
        String s1 = cleanExpression(split[0].substring(7));
        String s2 = cleanExpression(split[1].substring(0, split[1].length() - 1));
        a = parser.LOGIC_PARSER.parse(s1);
        b = parser.LOGIC_PARSER.parse(s2);
        return this;
    }

    /**
     * Removes the "choice( )" string and replaces the predicates with their indices (negated predicates become
     * ~index. True and false are replaced with 't' and 'f' respectively
     *
     * @param str The string to be cleaned
     * @return The cleaned string
     */
    // @NotNull
    private String cleanChoiceExpression(String str) {
        String s = cleanExpression(str);
        return s.substring(7, s.length() - 1);
    }

    // @NotNull
    public String cleanExpression(String str) {
        final String[] s = {str};
        predMap.forEach((k, i) -> s[0] = s[0].replace(k, i.toString()));
        s[0] = s[0].replace("true", "\'t\'");
        s[0] = s[0].replace("false", "\'f\'");

        // Double negation replacement. NB: Has to be in this order.
        // Double negatives of the form not(not(...))
        s[0] = s[0].replaceAll("not\\s*\\(\\s*not\\s*\\((.*)\\)\\)", "$1");

        // Double negatives of the form not( not p )
        s[0] = s[0].replaceAll("not\\s*\\(\\s*not\\s*(.*)\\)", "$1");

        // Replace not pred with ~pred
        s[0] = s[0].replaceAll("not\\s*\\((\\d+)\\)", "\"~$1\"");
        s[0] = s[0].replaceAll("not\\s*(\\d+)", "\"~$1\"");
        return s[0];
    }

    public int findClosingParen(char[] text, int openPos) {
        int closePos = openPos;
        int counter = 1;
        while (counter > 0) {
            char c = text[++closePos];
            if (c == '(') {
                counter++;
            } else if (c == ')') {
                counter--;
            }
        }
        return closePos;
    }

    public LogicParser getParser() {
        return parser;
    }
}