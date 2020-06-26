package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class UnaryOperator extends AstExpression implements Expression, Statement {

	public enum Kind {
		MINUS, NOT, OPT, PRE_INC, POST_INC, PRE_DEC, POST_DEC, ALWAYS, EVENTUALLY
	}

	public Kind kind;
	public Expression operand;
	public boolean expanded = false;

	public UnaryOperator(Kind kind, Expression operand) {
		super();
		this.kind = kind;
		this.operand = operand;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		switch (kind) {
		case MINUS:
			out.print("-");
			break;
		case NOT:
			out.print("!");
			break;
		case OPT:
			out.print("?");
			break;
		case PRE_DEC:
			out.print("--");
			break;
		case PRE_INC:
			out.print("++");
			break;
		case ALWAYS:
			out.print("[]");
			break;
		case EVENTUALLY:
			out.print("<>");
			break;
		default:
		}

		operand.prettyPrint(out);

		switch (kind) {
		case POST_DEC:
			out.print("--");
			break;
		case POST_INC:
			out.print("++");
			break;
		default:
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitUnaryOperator(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		throw new UnsupportedOperationException();
	}
	
}
