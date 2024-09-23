/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tests;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import tests.LexerTests;

/**
 *
 * @author hexaredecimal
 */
public class Test {

	public static void run() {
		// Run it
		Object[][] testers = {{LexerTests.class, new LexerTests()}, {ParserTests.class, new ParserTests()}};

		for (Object[] tester : testers) {
			Class test_class = (Class) tester[0];
			Tester test_instance = (Tester) tester[1];
			Thread t = new Thread(() -> {
				perform(test_class, test_instance);
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException ex) {
				Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

	private static void perform(Class clz, Tester tester) {
		Instant start = Instant.now();
		System.out.println("===============================================");
		System.out.println("running tests for: " + clz.getName());
		System.out.println("start time: " + start);
		System.out.println("");

		tester.run();
		Instant end = Instant.now();
		System.out.println("end time: " + end);
		System.out.println("");

		Duration time = Duration.between(start, end);
		System.out.println("duration (ms): " + time.toMillis());
		System.out.println("===============================================");
	}
}
