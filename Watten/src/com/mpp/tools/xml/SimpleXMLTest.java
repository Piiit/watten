package com.mpp.tools.xml;

import static org.junit.Assert.*;
import java.text.ParseException;
import org.junit.Test;

public class SimpleXMLTest {

	@Test
	public void test() {
		String input = "<root><x>--- HELP----\n123" + "\nasdf asdf " + "</x></root>";
		SimpleXML xml = new SimpleXML(input);
		try {
			xml.parse();
			assertEquals("--- HELP----\n123\nasdf asdf ", xml.root.getNode("x").getData());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
