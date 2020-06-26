package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class NumberLiteral extends AstExpression implements Expression {

	public Number value;
	public int typeInfo;
	
	public NumberLiteral(Number value) {
		this.value=value;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(value);
	}

	@Override
	public Number executeGeneric(VirtualFrame virtualFrame) {
		return value;
	}
	
}
