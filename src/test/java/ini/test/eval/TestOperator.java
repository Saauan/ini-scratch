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
	
	public void testMulOverflow() {
		testFile("ini/examples/calculus/fac_func.ini",
				(p, out) -> assertEquals("Fac(20)=2432902008176640000" + nl, out));
	}

	public void testDiv() {
		testFile("ini/truffle/operator/TestDiv.ini",
				(p, out) -> assertEquals("n3 = n1 / n2 = 5" + nl + "n4 = 10/2 = 5" + nl, out));
	}

	public void testSub() {
		testFile("ini/truffle/operator/TestSub.ini",
				(p, out) -> assertEquals("n3 = n1 - n2 = 2" + nl + "n4 = 5-3 = 2" + nl, out));
	}
	
	public void testLogicalNot() {
		testFile("ini/truffle/operator/TestLogicalNot.ini",
				(p, out) -> assertEquals("!false : true" + nl + 
						"!true : false" + nl, out));
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
	
	public void testNotEquals() {
		testFile("ini/truffle/operator/TestNotEquality.ini",
				(p, out) -> assertEquals("5!=5 : false" + nl + 
						"true!=true : false" + nl + 
						"\"hey\"!=\"hey\" : false" + nl + 
						"3.14!=3.14 : false" + nl + 
						"5!=4 : true" + nl + 
						"true!=false : true" + nl + 
						"\"hey\"!=\"ho\" : true" + nl + 
						"3.14!=3.42 : true" + nl + 
						"\"3.14\"!=3.14 : true" + nl, out));
	}
	
	public void testLowerThan() {
		testFile("ini/truffle/operator/TestLowerThan.ini",
				(p, out) -> assertEquals("5<6 :true" + nl + 
						"6<5 :false" + nl + 
						"5.0<5.1 :true" + nl + 
						"\"abcd\"<\"abdc\" :true" + nl + 
						"\"abdc\"<\"abcd\" :false" + nl, out));
	}
	
	public void testLowerOrEqual() {
		testFile("ini/truffle/operator/TestLowerOrEqual.ini",
				(p, out) -> assertEquals("5<=6 :true" + nl + 
						"6<=5 :false" + nl + 
						"5<=5 :true" + nl + 
						"5.0<=5.1 :true" + nl + 
						"5.0<=5.0 :true" + nl + 
						"\"abcd\"<=\"abdc\" :true" + nl + 
						"\"abcd\"<=\"abcd\" :true" + nl, out));
	}
	
	public void testGreaterThan() {
		testFile("ini/truffle/operator/TestGreaterThan.ini",
				(p, out) -> assertEquals("5>6 :false" + nl + 
						"6>5 :true" + nl + 
						"5.0>5.1 :false" + nl + 
						"\"abcd\">\"abdc\" :false" + nl + 
						"\"abdc\">\"abcd\" :true" + nl, out));
	}
	
	public void testGreaterOrEqual() {
		testFile("ini/truffle/operator/TestGreaterOrEqual.ini",
				(p, out) -> assertEquals("5>=6 :false" + nl + 
						"6>=5 :true" + nl + 
						"5>=5 :true" + nl + 
						"5.0>=5.1 :false" + nl + 
						"5.0>=5.0 :true" + nl + 
						"\"abcd\">=\"abdc\" :false" + nl + 
						"\"abcd\">=\"abcd\" :true" + nl, out));
	}
	
	public void testLogicalAnd() {
		testFile("ini/truffle/operator/TestLogicalAnd.ini",
				(p, out) -> assertEquals("true && true == true" + nl + 
						"false && true == false" + nl + 
						"true && false == false" + nl + 
						"false && false == false" + nl + 
						"false && printAndReturnFalse() == false" + nl, out));
	}
	
	public void testLogicalOr() {
		testFile("ini/truffle/operator/TestLogicalOr.ini",
				(p, out) -> assertEquals("true || true == true" + nl + 
						"false || true == true" + nl + 
						"true || false == true" + nl + 
						"false || false == false" + nl + 
						"true || printAndReturnFalse() == true" + nl, out));
	}
}
