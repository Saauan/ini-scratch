package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName="char")
public class CharLiteral extends AstExpression implements Expression {

	public char value;

	public CharLiteral(char value) {
		super();
		this.value = value;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("'" + value + "'");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitCharLiteral(this);
	}
	
	@Override
	public char executeChar(VirtualFrame virtualFrame) {
		return value;
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		return value;
	}

	
}
