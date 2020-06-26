package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ConditionalExpression extends AstExpression implements Expression {
	public Expression condition;
	public Expression trueExpression;
	public Expression falseExpression;

	public ConditionalExpression(Expression condition, Expression trueExpression, Expression falseExpression) {
		super();
		this.condition = condition;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.println(condition+"?"+trueExpression+":"+falseExpression);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitConditionalExpression(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
