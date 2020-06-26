package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ThisLiteral extends AstExpression implements Expression {

	public ThisLiteral() {
		super();
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("this");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitThisLiteral(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
