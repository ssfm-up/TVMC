package za.ac.up.cs;

import cnf.Formula;
import cnf.Var;
import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Unary;

import java.util.Map;
import java.util.Objects;

import static cnf.CNF.*;

/**
 * Created by Matthias on 2016/06/15.
 * Spotlight
 */
public class LogicParser {
    private final Parser<Void> IGNORED = Scanners.WHITESPACES.skipMany();

    // Non negated integer predicates
    private final Parser<Formula> PREDICATE = Terminals.IntegerLiteral.PARSER.map(new org.codehaus.jparsec.functors.Map<String, Formula>() {
        public Formula map(String s) {
//            System.out.println("INT");
            int pred = Integer.parseInt(s);
            return or(and(var(predVar(pred, bound, false)), unknownVar) , and(var(predVar(pred, bound, true)), neg(var(predVar(pred, bound, false))))) ;
        }
    });

    // Boolean atoms, 't' and 'f'
    private final Parser<Formula> BOOL = Terminals.CharLiteral.PARSER.map(new org.codehaus.jparsec.functors.Map<Character, Formula>() {
        public Formula map(Character c) {
//            System.out.println("CHAR");
            if (c == 't') {
                return trueVar;
            } else {
                assert c == 'f';
                return falseVar;
            }
        }
    });

    // Atoms of the form ~\d+. Negated predicates e.g. ~0
    private final Parser<Formula> STRING = Terminals.StringLiteral.PARSER.map(new org.codehaus.jparsec.functors.Map<String, Formula>() {
        public Formula map(String s) {
//            System.out.println("STRING");
//            if (s.equals("t")) return trueVar;
//            if (s.equals("f")) return falseVar;
            if (s.startsWith("~")) {
                int pred = Integer.parseInt(s.substring(1));
                return or(and(var(predVar(pred, bound, false)), unknownVar), and(neg(var(predVar(pred, bound, true))), neg(var(predVar(pred, bound, false))))) ;
            }

            int pred = Integer.parseInt(s);
            return or(and(var(predVar(pred, bound, false)), unknownVar) , and(var(predVar(pred, bound, true)), neg(var(predVar(pred, bound, false))))) ;
        }
    });

    private final Terminals OPERATORS = Terminals.operators("or", "and", "not", "(", ")");
    private final Parser<?> TOKENIZER = OPERATORS.tokenizer().cast()
            .or(Terminals.IntegerLiteral.TOKENIZER)
            .or(Terminals.CharLiteral.SINGLE_QUOTE_TOKENIZER)
            .or(Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER);
    public final Parser<Formula> LOGIC_PARSER = logicParser(PREDICATE.or(BOOL).or(STRING)).from(TOKENIZER, IGNORED);
    private final Map<String, Integer> predMap;
    private final Map<String, Var> vars;
    private final Formula trueVar;
    private final Formula falseVar;
    private final Formula unknownVar;
    private final int bound;

    public LogicParser(Map<String, Integer> predMap, Map<String, Var> vars, Formula trueVar, Formula falseVar, Formula unknownVar, int bound) {
        this.predMap = predMap;
        this.vars = vars;
        this.trueVar = trueVar;
        this.falseVar = falseVar;
        this.unknownVar = unknownVar;
        this.bound = bound;
    }


    private Parser<?> term(String... names) {
        return OPERATORS.token(names);
    }

    private <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }

    private Parser<Formula> logicParser(Parser<Formula> atom) {
        Parser.Reference<Formula> ref = Parser.newReference();
        Parser<Formula> unit = ref.lazy().between(term("("), term(")")).or(atom);
        Parser<Formula> parser = new OperatorTable<Formula>()
                .infixl(op("or", BinaryOperator.OR), 10)
                .infixl(op("and", BinaryOperator.AND), 20)
                .prefix(op("not", UnaryOperator.NOT), 30)
                .build(unit);
        ref.set(parser);
        return parser;
    }

    private enum BinaryOperator implements Binary<Formula> {
        OR {
            public Formula map(Formula a, Formula b) {
                return or(a, b);
            }
        },
        AND {
            public Formula map(Formula a, Formula b) {
                return and(a, b);
            }
        }
    }

    private enum UnaryOperator implements Unary<Formula> {
        NOT {
            public Formula map(Formula n) {
                return neg(n);
            }
        }
    }

    private Var getNamedVar(String s) {
        Var x = vars.get(s);
        if (x == null) {
            x = freshVar();
            vars.put(s, x);
        }
        return x;
    }

    private Var predVar(int pred, int bound, boolean known) {
        return getNamedVar("p_" + pred + "_" + bound + "_" + (known ? "b" : "u"));
    }

}
