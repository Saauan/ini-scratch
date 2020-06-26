package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class BooleanLiteral extends AstExpression implements Expression {

	public boolean value;
	
	public BooleanLiteral(boolean value) {
		super();
		this.value=value;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(value?"true":"false");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitBooleanLiteral(this);
	}

	@Override
	public boolean executeBoolean(VirtualFrame virtualFrame) {
		return value;
	}
	
	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		return value;
	}
	
}
