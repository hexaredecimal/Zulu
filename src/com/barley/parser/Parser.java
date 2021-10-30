package com.barley.parser;

import com.barley.ast.*;
import com.barley.runtime.*;
import com.barley.utils.*;

import javax.print.attribute.standard.NumberUp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public final class Parser {

    private static final Token EOF = new Token(TokenType.EOF, "", -1);

    public HashMap<String, Function> methods;

    private final List<Token> tokens;
    private final int size;

    private int pos;

    private String module;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
        methods = new HashMap<>();
        module = null;
    }

    public List<AST> parse() {
        final List<AST> result = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            AST expr = declaration();
            result.add(expr);
            consume(TokenType.DOT, "unterminated term.\n    where term: \n        " + expr);
        }
        Modules.put(module, methods);
        return result;
    }

    public List<AST> parseExpr() {
        final List<AST> result = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            AST expr = expression();
            result.add(expr);
            consume(TokenType.DOT, "unterminated term.\n    where term: \n        " + expr);
        }
        return result;
    }

    private AST declaration() {
        Token current = get(0);
        if (match(TokenType.ATOM)) {
            return method(current.getText());
        } else if (match(TokenType.MINUS)) {
            if (match(TokenType.MODULE)) {
                consume(TokenType.LPAREN, "expected '(' before module name");
                module = expression().toString();
                consume(TokenType.RPAREN, "expected ')' after module name");
            }
            return new ConstantAST(new BarleyNumber(0));
        } else throw new BarleyException("BadCompiler", "bad declaration '" + current + "'");
    }

    private AST method(String name) {
        Clause clause = clause();
        consume(TokenType.STABBER, "error at '" + name + "' declaration");
        clause.setResult(expression());
        ArrayList<Clause> clauses = new ArrayList<>();
        if (methods.containsKey(name)) {
            clauses.addAll(((UserFunction) methods.get(name)).getClauses());
        }
        clauses.add(clause);
        methods.put(name, new UserFunction(clauses));
        return new ConstantAST(new BarleyNumber(0));
    }

    private AST expression() {
        return assignment();
    }

    private AST assignment() {
        AST result = conditional();

        while (true) {
            if (match(TokenType.EQ)) {
                result = new BindAST(result, conditional());
                continue;
            }
            break;
        }

        return result;
    }

    private AST conditional() {
        AST result = additive();

        if (match(TokenType.LT)) {
            return new BinaryAST(result, additive(), '<');
        }

        if (match(TokenType.GT)) {
            return new BinaryAST(result, additive(), '>');
        }

        if (match(TokenType.LTEQ)) {
            return new BinaryAST(result, additive(), 't');
        }

        if (match(TokenType.GTEQ)) {
            return new BinaryAST(result, additive(), 'g');
        }

        if (match(TokenType.EQEQ)) {
            return new BinaryAST(result, additive(), '=');
        }
        return result;
    }

    private AST additive() {
        AST result = multiplicative();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryAST(result, multiplicative(), '+');
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryAST(result, multiplicative(), '-');
                continue;
            }
            break;
        }

        return result;
    }

    private AST multiplicative() {
        AST result = unary();

        while (true) {
            // 2 * 6 / 3
            if (match(TokenType.STAR)) {
                result = new BinaryAST(result, unary(), '*');
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryAST(result, unary(), '/');
                continue;
            }
            break;
        }

        return result;
    }

    private AST unary() {
        if (match(TokenType.MINUS)) {
            return new UnaryAST(call(), '-');
        }

        return call();
    }

    private AST call() {
        AST result = remote();

        while (true) {
            if (lookMatch(0,TokenType.LPAREN)) {
                ArrayList<AST> args = arguments();
                result = new CallAST(result, args);
            }
            break;
        }

        return result;
    }

    private AST remote() {
        AST result = primary();

        while (true) {
            if (match(TokenType.COLON)) {
                result = new RemoteAST(result, primary());
                continue;
            }
            break;
        }

        return result;
    }

    private AST list() {
        LinkedList<AST> array = new LinkedList<>();
        while (!(match(TokenType.RBRACKET))) {
            array.add(expression());
            match(TokenType.COMMA);
        }
        return new ListAST(array);
    }

    private AST primary() {
        final Token current = get(0);
        if (match(TokenType.NUMBER)) {
            return new ConstantAST(new BarleyNumber(Double.parseDouble(current.getText())));
        }
        if (match(TokenType.STRING)) {
            return new ConstantAST(new BarleyString(current.getText()));
        }
        if (match(TokenType.LPAREN)) {
            AST result = expression();
            match(TokenType.RPAREN);
            return result;
        }
        if (match(TokenType.VAR)) {
            return new ExtractBindAST(current.getText());
        }
        if (match(TokenType.ATOM)) {
            int atom = addAtom(current.getText());
            return new ConstantAST(new BarleyAtom(atom));
        }

        if (match(TokenType.LBRACKET)) {
            return list();
        }
        throw new BarleyException("BadCompiler", "Unknown term\n    where term:\n        " + current);
    }

    private Clause clause() {
        ArrayList<AST> args = arguments();
        AST guard = null;
        if (match(TokenType.WHEN)) guard = expression();
        return new Clause(args, guard, null);
    }

    private int line() {
        return get(0).getLine();
    }

    private ArrayList<AST> arguments() {
        consume(TokenType.LPAREN, "error at ')' at line " + line());
        ArrayList<AST> args = new ArrayList<>();
        while (!(match(TokenType.RPAREN))) {
            args.add(expression());
            match(TokenType.COMMA);
        }
        return args;
    }

    private int addAtom(String atom) {
        return AtomTable.put(atom);
    }

    private boolean lookMatch(int pos, TokenType type) {
        return get(pos).getType() == type;
    }

    private Token consume(TokenType type, String text) {
        final Token current = get(0);
        if (type != current.getType()) throw new BarleyException("BadCompiler", text);
        pos++;
        return current;
    }

    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }

    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }
}
