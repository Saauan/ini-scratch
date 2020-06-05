package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.NodeChild;

import ini.ast.AstElement;
import ini.ast.Expression;
import ini.ast.Token;
import ini.parser.IniParser;

/**
 * Utility base class for operations that take two arguments (per convention called "left" and
 * "right"). For concrete subclasses of this class, the Truffle DSL creates two child fields, and
 * the necessary constructors and logic to set them.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class BinaryNode extends AstElement implements Expression {
	
	public static String symbol;
	
	public BinaryNode(IniParser parser, Token token) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		out.print("Binary Operator");
	}
	
	

}