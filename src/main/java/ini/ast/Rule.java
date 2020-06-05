package ini.ast;

import java.io.PrintStream;
import java.util.List;
import ini.Utils;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

public class Rule extends AstElement {

	@Children public final AstElement[] statements;
	@Child public AstElement guard;
	@Child public AtPredicate atPredicate;
	@Children public AstElement[] synchronizedAtsNames;

	public Rule(IniParser parser, Token token, AtPredicate atPredicate, AstElement guard,
			Sequence<AstElement> statements, List<Expression> synchronizedAtsNames) {
		super(parser, token);		
		this.atPredicate = atPredicate;
		this.guard = guard;
		this.statements = (AstElement[]) Utils.convertSequenceToArray(statements);
		if(synchronizedAtsNames != null) {
			this.synchronizedAtsNames = synchronizedAtsNames.toArray(new AstElement[0]);
		}
		else {
			this.synchronizedAtsNames = new AstElement[0];
		}
		this.nodeTypeId = AstNode.RULE;
	}

	public Rule(IniParser parser, Token token, Object atPredicate2, Expression g, AstElement[] l,
			Object synchronizedAtsNames2) {
				this.statements = null;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("  ");
		if(atPredicate!=null) {
			atPredicate.prettyPrint(out);
			if(guard!=null) {
				out.println(" ");
			}
		}
		if(guard!=null) {
			guard.prettyPrint(out);
		}
		out.println(" {");
		for(int i=0; i<this.statements.length; i++ ) {
			statements[i].prettyPrint(out);
		}
		out.println("  }");
	}

	@Override
	public String toString() {
		if(atPredicate!=null) {
			return atPredicate.toString();
		}
		if(guard!=null) {
			return guard.toString();
		}
		throw new RuntimeException("illegal AST node");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitRule(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		final int nbStatements = this.statements.length;
		for(int i=0; i<nbStatements; i++) {
			statements[i].executeVoid(virtualFrame);
		}
		return null;
	}
	
}
