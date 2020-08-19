package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.NodeChild;

import ini.ast.AstExpression;
import ini.ast.Expression;
import ini.ast.Visitor;

/**
 * Utility base class for operations that take two arguments (per convention called "left" and
 * "right"). For concrete subclasses of this class, the Truffle DSL creates two child fields, and
 * the necessary constructors and logic to set them.
 */
@NodeChild("valueNode")
public abstract class UnaryNode extends AstExpression implements Expression {
	
	public abstract AstExpression getValueNode();
	
	public abstract String getSymbol();
	
	@Override
	public void prettyPrint(PrintStream out) {
		getValueNode().prettyPrint(out);
		out.print(getSymbol());
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitUnaryNode(this);
	}
	

}