package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import ini.parser.IniParser;

@NodeInfo(description = "An operator that takes two arguments")
public class BinaryOperator extends AstElement implements Expression {

	public enum Kind {
		PLUS, MINUS, MULT, DIV, EQUALS, NOTEQUALS, LT, LTE, GT, GTE, AND, OR, MATCHES, CONCAT, IMPLIES
	}

	public Kind kind;
	public Expression left;
	public Expression right;

	public BinaryOperator(IniParser parser, Token token, Kind kind, Expression left, Expression right) {
		super(parser, token);
		this.kind = kind;
		this.left = left;
		this.right = right;
		this.nodeTypeId = AstNode.BINARY_OPERATOR;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		left.prettyPrint(out);
		switch (kind) {
		case PLUS:
			out.print("+");
			break;
		case MINUS:
			out.print("-");
			break;
		case MULT:
			out.print("*");
			break;
		case DIV:
			out.print("/");
			break;
		case EQUALS:
			out.print("==");
			break;
		case NOTEQUALS:
			out.print("!=");
			break;
		case LT:
			out.print("<");
			break;
		case GT:
			out.print(">");
			break;
		case LTE:
			out.print("<=");
			break;
		case GTE:
			out.print(">=");
			break;
		case AND:
			out.print("&&");
			break;
		case OR:
			out.print("||");
			break;
		case MATCHES:
			out.print("~");
			break;
		case CONCAT:
			out.print("&");
			break;
		case IMPLIES:
			out.print("=>");
			break;
		}
		right.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitBinaryOperator(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		throw new UnsupportedOperationException();
	}

	Number plus(Number n1, Number n2) {
		if (n1 instanceof Byte && n1 instanceof Byte) {
			return n1.byteValue() + n2.byteValue();
		}
		if (n1 instanceof Integer && n1 instanceof Integer) {
			return n1.intValue() + n2.intValue();
		}
		return n1.doubleValue() + n2.doubleValue();
	}

	Number mult(Number n1, Number n2) {
		if (n1 instanceof Byte && n1 instanceof Byte) {
			return n1.byteValue() * n2.byteValue();
		}
		if (n1 instanceof Integer && n1 instanceof Integer) {
			return n1.intValue() * n2.intValue();
		}
		return n1.doubleValue() * n2.doubleValue();
	}

	Number minus(Number n1, Number n2) {
		if (n1 instanceof Byte && n1 instanceof Byte) {
			return n1.byteValue() - n2.byteValue();
		}
		if (n1 instanceof Integer && n1 instanceof Integer) {
			return n1.intValue() - n2.intValue();
		}
		return n1.doubleValue() - n2.doubleValue();
	}

	Number minus(Number n) {
		if (n instanceof Byte) {
			return -n.byteValue();
		}
		if (n instanceof Integer) {
			return -n.intValue();
		}
		return -n.doubleValue();
	}
}
