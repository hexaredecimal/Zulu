package tests;

import com.zulu.parser.Lexer;
import com.zulu.parser.TokenType;
import com.zulu.utils.Token;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hexaredecimal
 */
public class LexerTests extends Tester {

	public LexerTests() {

	}

	private List<TestFunction> addFunctions() {
		List<TestFunction> functions = new LinkedList<>();
		functions.add(LexerTests::testSimpleArithmetic);
		functions.add(LexerTests::testAtoms);
		functions.add(LexerTests::testNumbers);
		functions.add(LexerTests::testComplextArithmetic);
		functions.add(LexerTests::testString);
		functions.add(LexerTests::testVariables);
		functions.add(LexerTests::testKeywords);
		return functions;
	}

	public void run() {
		List<TestFunction> functions = addFunctions();
		List<TestFunction> failures = functions
			.stream()
			.filter(function -> {
				boolean result = !function.apply();
				if (result) {
					System.out.println("Test failed!");
				}
				return result;
			}).toList();

		if (!failures.isEmpty()) {
			System.err.println(
				String.format(
					"\nTest failed: %02d/%02d test passed\n - %02d/%02d tests failed\n",
					functions.size() - failures.size(),
					functions.size(),
					failures.size(),
					functions.size()
				)
			);
			assert false;
		} else {
			System.out.println(String.format("\nTest passed: %02d/%02d test passed\n", functions.size(), functions.size()));
		}
	}

	private static boolean testKeywords() {
		System.out.println("running testKeywords()");
		String keywords = "module doc when receive case of end and or global not def opt defguard import ";
		keywords = keywords + "unitbase strict pack unpack ast extern binary_operation unary_operation inline type";
		Lexer lexer = new Lexer(keywords);
		List<Token> tokens = lexer.tokenize();
		List<TokenType> keyword_types = new LinkedList<>();
		keyword_types.add(TokenType.MODULE);
		keyword_types.add(TokenType.MODULEDOC);
		keyword_types.add(TokenType.WHEN);
		keyword_types.add(TokenType.RECIEVE);
		keyword_types.add(TokenType.CASE);
		keyword_types.add(TokenType.OF);
		keyword_types.add(TokenType.END);
		keyword_types.add(TokenType.AND);
		keyword_types.add(TokenType.OR);
		keyword_types.add(TokenType.GLOBAL);
		keyword_types.add(TokenType.NOT);
		keyword_types.add(TokenType.DEF);
		keyword_types.add(TokenType.OPT);
		keyword_types.add(TokenType.DEFGUARD);
		keyword_types.add(TokenType.IMPORT);
		keyword_types.add(TokenType.UNIT);
		keyword_types.add(TokenType.STRICT);
		keyword_types.add(TokenType.PACK);
		keyword_types.add(TokenType.UNPACK);
		keyword_types.add(TokenType.AST);
		keyword_types.add(TokenType.EXTERN);
		keyword_types.add(TokenType.BINARY_OPERATION);
		keyword_types.add(TokenType.UNARY_OPERATION);
		keyword_types.add(TokenType.INLINE);
		keyword_types.add(TokenType.TYPE);
		keyword_types.add(TokenType.EOF); // Just to satisfy the test. Fix this

		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			if (keyword_types.contains(token.getType())) {
				tokens.remove(token);
				keyword_types.remove(token.getType());
				i--;
			}
		}

		return tokens.isEmpty() && keyword_types.isEmpty();
	}

	private static boolean testVariables() {
		System.out.println("running testVariables()");
		Lexer lexer = new Lexer("Foo Bar");
		List<Token> tokens = lexer.tokenize();

		var first = tokens.remove(0);
		var second = tokens.remove(0);
		return first.getType() == TokenType.VAR
			&& first.getText().equals("Foo")
			&& second.getType() == TokenType.VAR
			&& second.getText().equals("Bar");
	}

	private static boolean testString() {
		System.out.println("running testString()");

		Lexer lexer = new Lexer("\"Hello, world\" \"This is zulu\"");
		List<Token> tokens = lexer.tokenize();

		var first = tokens.remove(0);
		var second = tokens.remove(0);
		return first.getType() == TokenType.STRING
			&& second.getType() == TokenType.STRING;
	}

	private static boolean testNumbers() {
		System.out.println("running testNumbers()");

		Lexer lexer = new Lexer("120 55.30");
		List<Token> tokens = lexer.tokenize();

		var first = tokens.remove(0);
		var second = tokens.remove(0);
		return first.getType() == TokenType.NUMBER
			&& first.getText().equals("120")
			&& second.getType() == TokenType.NUMBER
			&& second.getText().equals("55.30");
	}

	private static boolean testAtoms() {
		System.out.println("running testAtoms()");

		Lexer lexer = new Lexer("hello world");
		List<Token> tokens = lexer.tokenize();

		var first = tokens.remove(0);
		var second = tokens.remove(0);
		return first.getType() == TokenType.ATOM
			&& first.getText().equals("hello")
			&& second.getType() == TokenType.ATOM
			&& second.getText().equals("world");
	}

	private static boolean testComplextArithmetic() {
		System.out.println("running testComplextArithmetic()");

		Lexer lexer = new Lexer("50 / 2 * (5 * 2 + 77) * 700");
		List<Token> tokens = lexer.tokenize();
		var first = tokens.remove(0);
		var operator = tokens.remove(0);
		var last = tokens.remove(0);
		var first_part = first.getType() == TokenType.NUMBER // 50 / 2
			&& last.getType() == TokenType.NUMBER
			&& operator.getType() == TokenType.SLASH;
		var lbrace = tokens.remove(1);
		first = tokens.remove(1);
		operator = tokens.remove(1);
		last = tokens.remove(1);
		var next_operator = tokens.remove(1);
		var final_digit = tokens.remove(1);
		var rbrace = tokens.remove(1);
		var second_part = lbrace.getType() == TokenType.LPAREN
			&& rbrace.getType() == TokenType.RPAREN
			&& first.getType() == TokenType.NUMBER // 5 * 2
			&& last.getType() == TokenType.NUMBER
			&& operator.getType() == TokenType.STAR
			&& next_operator.getType() == TokenType.PLUS
			&& final_digit.getType() == TokenType.NUMBER;
		var star1 = tokens.remove(0);
		var star2 = tokens.remove(0);
		final_digit = tokens.remove(0);

		var third_part = star1.getType() == star2.getType()
			&& star1.getType() == TokenType.STAR
			&& final_digit.getType() == TokenType.NUMBER;

		return first_part && second_part && third_part;
	}

	private static boolean testSimpleArithmetic() {
		System.out.println("running testSimpleArithmetic()");

		Lexer lexer = new Lexer("10 + 5");
		List<Token> tokens = lexer.tokenize();
		var first = tokens.remove(0);
		var operator = tokens.remove(0);
		var last = tokens.remove(0);
		return first.getType() == TokenType.NUMBER
			&& last.getType() == TokenType.NUMBER
			&& operator.getType() == TokenType.PLUS;
	}
}
