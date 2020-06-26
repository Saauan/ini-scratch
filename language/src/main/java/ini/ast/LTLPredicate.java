package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

public class LTLPredicate extends NamedElement {

	public AstNode expression;

	public LTLPredicate(String name, AstNode expression) {
		super(name);
		this.expression = expression;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitLTLPredicate(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prettyPrint(PrintStream out) {
		// TODO Auto-generated method stub
		
	}
	
}
