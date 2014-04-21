package com.mpp.testing;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * This is the user interface with different inputFields which scan data from stdin and print different
 * shaped boxes, lines and fields around text.
 * It is possible to set it silent in order to be able to test input and output within other streams.
 * @author Peter Moser (pemoser)
 */
public class UserInterface {
	private static Scanner scanner;
	private static String inputString;
	private static boolean silent = false;
	
	
	/**
	 * Prints an information text and waits for a valid string input from stdin
	 * @param info
	 * @return User's input as a string.
	 */
	public static String inputField(String info) {
		if (silent) return "";
		System.out.print("> " + info + ": ");
		scanner = new Scanner(System.in);
		inputString = scanner.nextLine();
		return inputString;
	}
	

	/**
	 * Prints an information text and waits for an ENTER from stdin
	 * @param info
	 */
	public static void inputFieldEnter(String info) {
		if (silent) return;
		System.out.print("> " + info);
		scanner = new Scanner(System.in);
		inputString = scanner.nextLine();
	}
	
	
	/**
	 * Prints an information text and waits for a valid float-string input from stdin
	 * @param info
	 * @return User's input as a float.
	 */
	public static float inputFieldFloat(String info) {
		if (silent) return 0;
		String input;
		float value = 0;
		boolean ok = true;
		scanner = new Scanner(System.in);
		do {
			System.out.print("> " + info + ": ");
			input = scanner.next();
			value = 0;
			try {
				value = Float.parseFloat(input);
				ok = true;
			} catch (NumberFormatException ex) {
				System.out.println("> Please enter a valid number.");
				ok = false;
			}
		} while (!ok);
		return value;    
	}
	
	
	/**
	 * Prints an information text and waits for a valid integer-string input from stdin
	 * @param info
	 * @return User's input as a integer.
	 */
	public static int inputFieldInteger(String info) {
		if (silent) return 0;
		String input;
		int value = 0;
		boolean ok = true;
		scanner = new Scanner(System.in);
		do {
			try {
				System.out.print("> " + info + ": ");
				input = scanner.next();
				value = 0;
				value = Integer.parseInt(input);
				ok = true;
			} catch (NumberFormatException ex) {
				System.out.println("> Please enter a valid number.");
				ok = false;
			} catch (NoSuchElementException e) {
				throw new NoSuchElementException("UserInterface: Can't read from stdin!");
			}
		} while (!ok);
		return value;    
		
	}
	
	
	/**
	 * Prints a new line.
	 */
	public static void printNL() {
		if (silent) return;
		System.out.println();
	}
	
	
	/**
	 * Prints a dashed line of a certain length.
	 * @param length how many "-"?
	 */
	public static void printLine(int length) {
		if (silent) return;
		for (int i = 0; i < length; i++) {
			System.out.print("-");
		}
	}
	
	
	/**
	 * Prints a box around a text
	 * @param what
	 */
	public static void printBox(String what) {
		if (silent) return;
		String x[] = what.split("\n");
		int longestLine = 0;
		
		for (String a : x) {
			if (a.length() > longestLine) {
				longestLine = a.length();
			}
		}
		
		if (longestLine == 0) {
			return;
		}
		
		System.out.print("+");
		printLine(longestLine+2);
		System.out.print("+");
		printNL();
		for (String a : x) {
			System.out.println(String.format("| %-"+longestLine+"s |", a));
		}
		System.out.print("+");
		printLine(longestLine+2);
		System.out.print("+");
		printNL();
	}
	
	
	/**
	 * Prints a line above and below a title text
	 * @param what
	 */
	public static void printTitle(String what) {
		if (silent) return;
		printNL();
		printLine(80);
		printNL();
		System.out.println(what);
		printLine(80);
		printNL();
	}
	
	
	/**
	 * Prints a information in a single line.
	 * @param what
	 */
	public static void printInfo(String what) {
		if (silent) return;
		System.out.println("+ " + what);
	}

	
	/**
	 * If silent is set to true no user output and input will take place if this classes' methods were called.
	 * @param s
	 */
	public static void setSilent(boolean s) {
		silent = s;
	}

}
