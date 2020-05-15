package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

public class LTLPredicate extends NamedElement {

	public AstNode expression;

	public LTLPredicate(IniParser parser, Token token, String name, AstNode expression) {
		super(parser, token, name);
		this.expression = expression;
		this.nodeTypeId = AstNode.PREDICATE;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("predicate " + name + " "+STRING_LITERAL);
		if (annotations != null) {
			out.print(" " + annotations);
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitLTLPredicate(this);
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
