package xml;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple XML parser (some ideas and code pieces taken from the book
 * "Language implementation patterns" by Terence Parr) 
 * @author Peter Moser (pemoser)
 */
public class SimpleXML {
	
	private String input;
	private String currentTag;
	private int index;
	private char c;
	private static final char EOF = (char)-1;
	public Node root = null;
	private Node nodePointer = root;
	
	
	/**
	 * Constructor of a simple XML parser with an input string to parse.
	 * @param iInput
	 */
	public SimpleXML(String iInput) {
		input = iInput;
		index = 0;
		if (input.length() > 0) {
			c = input.charAt(0);
		}
	}
	
	
	/**
	 * Create a simple XML tag around a data-string. 
	 * Example: <code>&lt;node&gt;data of it&lt;/node&gt;</code>
	 * @param tagName The name of the tag, like "node" in the example
	 * @param tagData The data within the tags.
	 * @return a string similar to the one in the example
	 */
	public static String createTag(String tagName, String tagData) {
		return "<" + tagName + ">" + tagData + "</" + tagName + ">";
	}

	
	/**
	 * Create a simple XML tag around a data-string. 
	 * Example: <code>&lt;node&gt;3&lt;/node&gt;</code>
	 * @param tagName The name of the tag, like "node" in the example
	 * @param tagData The data within the tags as integer.
	 * @return a string similar to the one in the example
	 */
	public static String createTag(String tagName, int tagData) {
		return "<" + tagName + ">" + tagData + "</" + tagName + ">";
	}
	
	
	/**
	 * Parse the input string by jumping from one token to the next and build a tree with nodes.
	 * This tree can also have nested nodes and empty nodes, but throws an exception if the order isn't 
	 * correct.
	 * @see xml.Node
	 * @see xml.Token
	 * @throws ParseException if a tag exits which has never started or started in wrong order.
	 */
	public void parse() throws ParseException {
		Token t;
		do {
			t = nextToken();
			switch (t.getType()) {
				case TAG_ENTER:
					addNode(new Node(t.getData()));
					currentTag = t.getData();
					break;
				case TAG_EXIT:
					if (t.getData().compareToIgnoreCase(currentTag) != 0) {
						throw new ParseException("Expected </" + currentTag + ">, but </" + t.getData() + "> found!", -1);
					}
					nodePointer = nodePointer.getParent();
					if (nodePointer != null) {
						currentTag = nodePointer.getName();
					}
					break;
				case TAG_DATA:
					if (nodePointer != null) {
						nodePointer.setData(t.getData());
					}
					break;
			}
		} while (t.getType() != Token.Type.EOF);
	}

	
	/**
	 * Return a list of all nodes within a parent node with the provided name.
	 * @param name
	 * @return a list of all nodes within a parent node with the provided name.
	 */
	public ArrayList<Node> getChildrenByName(String name) {
		ArrayList<Node> tmp = new ArrayList<Node>();
		for (Node n : root.getChildren()) {
			if (n.getName().compareToIgnoreCase(name) == 0) {
				tmp.add(n);
			}
		}
		return tmp;
	}

	
	/**
	 * Find and return the next token starting from the current position inside the input string.
	 * Jumps all white spaces.
	 * @return found token.
	 * @throws ParseException if the input string has an invalid format.
	 */
	private Token nextToken() throws ParseException {
		while (c != EOF) {
			switch (c) {
				case ' ': case '\t': case '\n': case '\r':
					whiteSpace();
					continue;
				case '<':
					consume();
					return tagName();
				default:
					return tagData();
			}
		}
		return new Token(Token.Type.EOF, "");
	}

	
	/**
	 * Consumes a single character and increments the string pointer.
	 * Checks if we have reached the end of the string.
	 */
	private void consume() {
		index++;
		if (index >= input.length()) {
			c = EOF;
		} else {
			c = input.charAt(index);
		}
	}
	
	
	/**
	 * Checks if the provided character matches with the character at current position.
	 * Consumes it if possible otherwise a ParseException will be thrown.
	 * @param x
	 * @throws ParseException
	 */
	private void match(char x) throws ParseException {
		if (c == x) {
			consume();
		} else {
			throw new ParseException("Expected " + x + ", but " +c+ " found! Position = " + index + "\nINPUT = " + input, index);
		}
	}
	
	
	/**
	 * Consume all white spaces, like space, tabs, new lines and carriage returns...
	 */
	private void whiteSpace() {
		while (c == ' ' || c == '\t' || c == '\n' || c == '\r')
			consume();
	}
	
	
	/**
	 * Check if the current character is a default English letter.
	 * @return true if it is a default English letter.
	 */
	private boolean isLetter() {
		return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
	}
	
	
	/**
	 * Find the next tagName - token... 
	 * @return Found token
	 * @throws ParseException if inside this token a invalid character has been found.
	 */
	private Token tagName() throws ParseException {
		StringBuilder buf = new StringBuilder();
		Token.Type type = Token.Type.TAG_ENTER;
		if (c == '/') {
			consume();
			type = Token.Type.TAG_EXIT;
		}
		do {
			buf.append(c);
			consume();
		} while( isLetter() );
		match('>');
		return new Token(type, buf.toString());
	}
	
	
	/**
	 * Find the next tagData - token...
	 * No ParseException here, because a tag data can store every kind of string.
	 * @return Found token.
	 */
	private Token tagData() {
		StringBuilder buf = new StringBuilder();
		do {
			buf.append(c);
			consume();
		} while( c != '<' && c != EOF);
		return new Token(Token.Type.TAG_DATA, buf.toString());
	}
	
	
	/**
	 * Add a node to a node tree. Set the root if it's the first node.
	 * Automatically append child nodes if the current node pointer hasn't been set yet.
	 * @param n
	 */
	private void addNode(Node n) {
		if (root == null) {
			root = n;
		}  
		if (nodePointer != null) {
			nodePointer.addChild(n);
		}
		nodePointer = n;
	}
	
	private int indent = 0;
	private String indentedOutput = "";
	
	private void toStringIndented(Node node) {
		indentedOutput += String.format("%" + (indent == 0 ? "" : indent * 4) + "s<%s>", "", node.getName());
		if(node.hasChildren()) {
			indentedOutput += "\n";
			for (Node child : node.getChildren()) {
				indent++;
				toStringIndented(child);
				indent--;
			}
			indentedOutput += String.format("%" + (indent == 0 ? "" : indent * 4) + "s</%s>\n", "", node.getName());
		} else {
			indentedOutput += String.format("%s</%s>\n", node.getData(), node.getName());
		}
	}
	
	public String toStringIntented() {
		indent = 0;
		indentedOutput = "";
		toStringIndented(root);
		return indentedOutput;
	}
	
}
