package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SubArrayAccess extends AstElement {

	public Expression targetExpression;
	public Expression minExpression;
	public Expression maxExpression;

	public SubArrayAccess(Expression targetExpression, Expression minExpression,
			Expression maxExpression) {
		super();
		this.targetExpression = targetExpression;
		this.minExpression = minExpression;
		this.maxExpression = maxExpression;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		targetExpression.prettyPrint(out);
		out.print("[");
		minExpression.prettyPrint(out);
		out.print("..");
		maxExpression.prettyPrint(out);
		out.print("]");
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitSubArrayAccess(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}

}
