package ini.test.eval;

import ini.test.IniTestCase;

public class TestBinary extends IniTestCase {

	public TestBinary(String name) {
		super(name);
	}
	
	public void testAdd() {
		testFile("ini/truffle/binary/TestAdd.ini", 
				(p, out) -> assertEquals("n3 = n1 + n2 = 8" + nl + "n4 = 5+3 = 8" + nl, out));
	}
	
	public void testMul() {
		testFile("ini/truffle/binary/TestMul.ini", 
				(p, out) -> assertEquals("n3 = n1 * n2 = 15" + nl + "n4 = 5*3 = 15" + nl, out));
	}
	
	public void testDiv() {
		testFile("ini/truffle/binary/TestDiv.ini", 
				(p, out) -> assertEquals("n3 = n1 / n2 = 5" + nl + "n4 = 10/2 = 5" + nl, out));
	}
	
	public void testSub() {
		testFile("ini/truffle/binary/TestSub.ini", 
				(p, out) -> assertEquals("n3 = n1 - n2 = 2" + nl + "n4 = 5-3 = 2" + nl, out));
	}

	public void testEquals() {
		testFile("ini/truffle/binary/TestEquality.ini",
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
