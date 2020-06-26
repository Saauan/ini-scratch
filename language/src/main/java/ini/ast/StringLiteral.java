package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class StringLiteral extends AstExpression implements Expression {

	public String value;

	public StringLiteral(String value) {
		super();
		this.value = value;
		this.value = this.value.replace("\\n", "\n");
		this.value = this.value.replace("\\r", "\r");
		this.value = this.value.replace("\\\"", "\"");
		this.value = this.value.replace("\\\\", "\\");
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("\"" + value + "\"");
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitStringLiteral(this);
	}
	
	@Override
	public String executeString(VirtualFrame frame) {
		return value;
	}

	@Override
	public String executeGeneric(VirtualFrame virtualFrame) {
		return value;
	}
}
