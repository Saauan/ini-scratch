package ini.test.eval;

import ini.test.IniTestCase;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}
	
	public void testPrintln() {
		testFile("ini/truffle/testPrintln.ini", 
				(p, out) -> assertEquals("hello world"+ System.lineSeparator() + "hello world", out));
	}

	public void testPrint() {
		testFile("ini/truffle/testPrint.ini",
				(p, out) -> assertEquals("hello worldhello world", out));
	}
	
	public void testVariable() {
		testFile("ini/truffle/testVariable.ini", 
				(p, out) -> assertEquals("5"
						+ System.lineSeparator() + "5"
						+ System.lineSeparator(), out));
	}
}
