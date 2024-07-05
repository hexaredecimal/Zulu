/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tests;

import com.erlava.ast.ConstantAST;
import com.erlava.ast.MethodAST;
import com.erlava.optimizations.Optimization;
import com.erlava.parser.Lexer;
import com.erlava.parser.Parser;
import com.erlava.parser.TokenType;
import com.erlava.runtime.BarleyNumber;
import com.erlava.utils.AST;
import com.erlava.utils.Token;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author hexaredecimal
 */
public class ParserTests extends Tester {

	public ParserTests() {

	}

	private List<TestFunction> addFunctions() {
		List<TestFunction> functions = new LinkedList<>();
		functions.add(ParserTests::testModuleSetup);
		functions.add(ParserTests::testSimpleFunction);
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

		if (failures.size() != 0) {
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

	public static boolean testSimpleFunction() {
		System.out.println("running testSimpleFunction()");
		String input = """
add(X, Y) -> X + Y.
    """;

		Lexer lexer = new Lexer(input);
		List<Token> tokens = lexer.tokenize();
		Parser parser = new Parser(tokens, input);
		List<AST> nodes = parser.parse();
		var func = nodes.remove(0); 

		return func instanceof MethodAST;
	}

	public static boolean testModuleSetup() {
		System.out.println("running testModuleSetup()");
		
		String input = """
 -module(test).
 -opt().
 -doc("Testing module setup").
  	""";

		Lexer lexer = new Lexer(input);
		List<Token> tokens = lexer.tokenize();
		Parser parser = new Parser(tokens, input);
		List<AST> nodes = parser.parse();

		var first = nodes.remove(0);
		var second = nodes.remove(0);
		var third = nodes.remove(0); 
		
		return first instanceof ConstantAST 
			&& second instanceof ConstantAST
			&& third instanceof ConstantAST;
	}
}
