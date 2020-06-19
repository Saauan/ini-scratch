package ini.test.eval;

import ini.test.IniTestCase;

public class TestControlflow extends IniTestCase {

	public TestControlflow(String name) {
		super(name);
	}
	
	public void testCase() {
		testFile("ini/truffle/controlflow/TestCase.ini", 
				(p, out) -> assertEquals("n<1" + nl + 
						"0 < n < 4" + nl + 
						"0 < n < 4" + nl + 
						"0 < n < 4" + nl + 
						"n==4" + nl + 
						"n > 4" + nl, out));
	}
}
