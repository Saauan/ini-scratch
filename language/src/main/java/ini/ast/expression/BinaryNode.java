package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.NodeChild;

import ini.ast.AstExpression;
import ini.ast.Expression;

/**
 * Utility base class for operations that take two arguments (per convention called "left" and
 * "right"). For concrete subclasses of this class, the Truffle DSL creates two child fields, and
 * the necessary constructors and logic to set them.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class BinaryNode extends AstExpression implements Expression {
	
	public abstract AstExpression getLeftNode();
	public abstract AstExpression getRightNode();
	
	public abstract String getSymbol();
	
	public BinaryNode() {
		super();
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		getLeftNode().prettyPrint(out);
		out.print(getSymbol());
		getRightNode().prettyPrint(out);
	}
	
	

}