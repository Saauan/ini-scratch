package ini.test.eval;

import ini.test.IniTestCase;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}
	
	public void testPrintln() {
		testFile("ini/truffle/TestPrintln.ini", 
				(p, out) -> assertEquals("hello world"+ nl + "hello world", out));
	}

	public void testPrint() {
		testFile("ini/truffle/TestPrint.ini",
				(p, out) -> assertEquals("hello worldhello world", out));
	}
	
	public void testVariable() {
		testFile("ini/truffle/TestVariable.ini", 
				(p, out) -> assertEquals("5"
						+ nl + "5"
						+ nl, out));
	}
}
