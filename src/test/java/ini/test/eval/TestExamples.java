package ini.test.eval;

import ini.test.IniTestCase;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}

	public void testPrint() {
		testFile("ini/truffle/testPrint.ini",
				(p, out) -> assertEquals("hello worldhello world", out));
	}
}
