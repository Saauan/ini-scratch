package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class BooleanLiteral extends AstExpression implements Expression {

	public boolean value;
	
	public BooleanLiteral(IniParser parser, Token token, boolean value) {
		super(parser, token);
		this.value=value;
		this.type = parser.types.BOOLEAN;
		this.nodeTypeId=AstNode.BOOLEAN_LITERAL;
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
