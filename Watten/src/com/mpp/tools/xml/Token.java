package com.mpp.tools.xml;


/**
 * Tokens are simple patterns used by a tokenizer (lexer). In this case we have a XML parser so there are 
 * only a few patterns to match: end of file, tag enter and exit, and tag data... 
 * @author Peter Moser (pemoser)
 *
 */
public class Token {
	public static enum Type { EOF, TAG_ENTER, TAG_EXIT, TAG_DATA };
	
	private String data;
	private Type type;
	
	public Token(Type type, String data) {
		this.data = data;
		this.type = type;
	}

	public String getData() {
		return data;
	}
	
	public Type getType() {
		return type;
	}
}
