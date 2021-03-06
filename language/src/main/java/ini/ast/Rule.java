package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import ini.Utils;
import ini.runtime.IniException;

@GenerateWrapper
public class Rule extends AstExpression {

	@Children public final AstElement[] statements;
	@Child public AstExpression guard;
	@Child public AtPredicate atPredicate;
	@Children public AstExpression[] synchronizedAtsNames;

	public Rule(AtPredicate atPredicate, AstExpression guard,
			Sequence<AstElement> statements, List<AstExpression> synchronizedAtsNames) {
		super();		
		this.atPredicate = atPredicate;
		this.guard = guard;
		this.statements = (AstElement[]) Utils.convertSequenceToArray(statements);
		if(synchronizedAtsNames != null) {
			this.synchronizedAtsNames = synchronizedAtsNames.toArray(new AstExpression[0]);
		}
		else {
			this.synchronizedAtsNames = new AstExpression[0];
		}
	}
	
	public Rule() {
		this.statements = null;
	}
	
	@Override public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new RuleWrapper(this, probeNode);
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
			out.print("   ");
			statements[i].prettyPrint(out);
			out.println("");
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

	/**
	 * Executes the statements if the guard evaluates to true
	 * 
	 * Returns whether the statements were executed or not
	 */
	@Override
	public boolean executeBoolean(VirtualFrame frame) {
		try {
			if (this.guard == null || this.guard.executeBoolean(frame)) {
				for(int i=0; i<statements.length; i++) {
					statements[i].executeVoid(frame);
				}
				return true;
			}
			else {
				return false;
			}
		} catch (UnexpectedResultException e) {
			throw IniException.typeError(this, this.guard);
		}

	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		return this.executeBoolean(virtualFrame);
	}

	
}
