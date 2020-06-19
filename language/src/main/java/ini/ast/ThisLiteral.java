package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ThisLiteral extends AstExpression implements Expression {

	public ThisLiteral(IniParser parser, Token token) {
		super(parser, token);
		this.type = parser.types.THREAD;
		this.nodeTypeId=AstNode.THIS_LITERAL;
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