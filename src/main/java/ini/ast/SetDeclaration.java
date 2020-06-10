package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SetDeclaration extends AstExpression implements Expression {

	public Expression lowerBound;
	public Expression upperBound;
	
	public SetDeclaration(IniParser parser, Token token, Expression lowerBound, Expression upperBound) {
		super(parser, token);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.nodeTypeId=AstNode.SET_DECLARATION;
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
