package ini.test.eval;

import ini.test.IniTestCase;

public class TestOperator extends IniTestCase {

	public TestOperator(String name) {
		super(name);
	}

	public void testAdd() {
		testFile("ini/truffle/operator/TestAdd.ini",
				(p, out) -> assertEquals("n3 = n1 + n2 = 8" + nl + "n4 = 5+3 = 8" + nl, out));
	}

	public void testMul() {
		testFile("ini/truffle/operator/TestMul.ini",
				(p, out) -> assertEquals("n3 = n1 * n2 = 15" + nl + "n4 = 5*3 = 15" + nl, out));
	}

	public void testDiv() {
		testFile("ini/truffle/operator/TestDiv.ini",
				(p, out) -> assertEquals("n3 = n1 / n2 = 5" + nl + "n4 = 10/2 = 5" + nl, out));
	}

	public void testSub() {
		testFile("ini/truffle/operator/TestSub.ini",
				(p, out) -> assertEquals("n3 = n1 - n2 = 2" + nl + "n4 = 5-3 = 2" + nl, out));
	}

	public void testEquals() {
		testFile("ini/truffle/operator/TestEquality.ini",
				(p, out) -> assertEquals("5==5 : true" + nl + 
						"true==true : true" + nl + 
						"\"hey\"==\"hey\" : true" + nl + 
						"3.14==3.14 : true" + nl + 
						"5==4 : false" + nl + 
						"true==false : false" + nl + 
						"\"hey\"==\"ho\" : false" + nl + 
						"3.14==3.42 : false" + nl + 
						"\"3.14\"==3.14 : false" + nl, out));
	}
	
	}
}
