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
	
	public void testFunction() {
		testFile("ini/truffle/TestFunction.ini", 
				(p, out) -> assertEquals("I'm in the function and the argument is 5 returning 6" + nl + 
						"The value returned is 6" + nl + 
						"I'm in the function and the argument is 5 returning 6" + nl + 
						"I'm in the function and the argument is 6 returning 7" + nl + 
						"Calling the same function twice : 7" + nl + 
						"I'm in the function and the argument is 5 returning 6" + nl + 
						"Calling the function once within println : 6" + nl + 
						"I'm in the function and the argument is 5 returning 6" + nl + 
						"I'm in the function and the argument is 6 returning 7" + nl + 
						"Calling the function twice within println : 7" + nl + 
						"I'm the printCalling print within println : I'm the print" + nl, out));
	}

	public void testRecursive() {
		testFile("ini/truffle/TestRecursive.ini", 
				(p, out) -> assertEquals(String.format("0%1$s1%1$s2%1$s3%1$s4%1$s5%1$s", nl), out));
	}
}
