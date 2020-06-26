package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SetDeclaration extends AstExpression implements Expression {

	public Expression lowerBound;
	public Expression upperBound;
	
	public SetDeclaration(Expression lowerBound, Expression upperBound) {
		super();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("[");
		lowerBound.prettyPrint(out);
		out.print("..");
		upperBound.prettyPrint(out);
		out.print("]");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitSetDeclaration(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
