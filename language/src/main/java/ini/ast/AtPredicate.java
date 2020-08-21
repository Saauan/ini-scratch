package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;

import ini.ast.at.At;
import ini.ast.at.AtConsume;
import ini.ast.at.AtEvery;

@GenerateWrapper
public class AtPredicate extends NamedElement {

	public enum Kind {
		UPDATE, EVERY, CRON, INIT, END, ERROR, READY, UPDATE_SYNC, CONSUME, USER_DEFINED
	}

	public List<Expression> outParameters;
	public Kind kind = null;
	public String identifier;
	@Child
	public At attachedAt = null;
	public boolean isAtAttached = false;

	public AtPredicate() {
		super(null);
	}
	
	public AtPredicate(String name, List<Expression> configurationArguments,
			List<Expression> runtimeArguments, String identifier) {
		super(name);
		this.identifier = identifier;
		this.annotations = configurationArguments;
		this.outParameters = runtimeArguments;
		if (this.outParameters != null) {
			for (Expression e : this.outParameters) {
				if (e instanceof Variable) {
					((Variable) e).setDeclaration(true);
				}
			}
		}
		if (name.equals("init")) {
			kind = Kind.INIT;
		}
		if (name.equals("consume")) {
			this.attachedAt = new AtConsume();
			kind = Kind.CONSUME;
		}
		if (name.equals("end")) {
			kind = Kind.END;
		}
		if (name.equals("ready")) {
			kind = Kind.READY;
		}
		if (name.equals("update")) {
			kind = Kind.UPDATE;
			throw new UnsupportedOperationException("Not implemented yet");
		}
		if (name.equals("update_sync")) {
			kind = Kind.UPDATE_SYNC;
			throw new UnsupportedOperationException("Not implemented yet");
		}
		if (name.equals("every")) {
			this.attachedAt= new AtEvery();
			this.isAtAttached = true;
		}
		if (name.equals("cron")) {
			kind = Kind.CRON;
			throw new UnsupportedOperationException("Not implemented yet");
		}
		if (name.equals("error")) {
			kind = Kind.ERROR;
		}
		if (kind == null) {
			kind = Kind.USER_DEFINED;
		}
	}
	
	@Override public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new AtPredicateWrapper(this, probeNode);
	  }

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("@" + name);
		out.print("[");
		prettyPrintList(out, annotations, ",");
		out.print("]");
		out.print("(");
		prettyPrintList(out, outParameters, ",");
		out.print(")");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAtPredicate(this);
	}


	@Override
	public void executeVoid(VirtualFrame frame) {
		
	}
	
}
