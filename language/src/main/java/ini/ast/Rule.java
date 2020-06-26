package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.Utils;

public class Rule extends AstElement {

	@Children public final AstElement[] statements;
	@Child public AstExpression guard;
	@Child public AtPredicate atPredicate;
	@Children public AstElement[] synchronizedAtsNames;

	public Rule(AtPredicate atPredicate, AstExpression guard,
			Sequence<AstElement> statements, List<Expression> synchronizedAtsNames) {
		super();		
		this.atPredicate = atPredicate;
		this.guard = guard;
		this.statements = (AstElement[]) Utils.convertSequenceToArray(statements);
		if(synchronizedAtsNames != null) {
			this.synchronizedAtsNames = synchronizedAtsNames.toArray(new AstElement[0]);
		}
		else {
			this.synchronizedAtsNames = new AstElement[0];
		}
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
	public void executeVoid(VirtualFrame virtualFrame) {
		for(int i=0; i<statements.length; i++) {
			statements[i].executeVoid(virtualFrame);
		}
	}
	
}
