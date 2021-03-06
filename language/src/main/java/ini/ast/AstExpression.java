package ini.ast;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import ini.IniTypesGen;

/**
 * An AstExpression is an AstElement that can produce a value
 */
@GenerateWrapper
public abstract class AstExpression extends AstElement {

	public AstExpression() {
		super();
	}
	
	/**
	 * The execute method when no specialization is possible. This is the most
	 * general case, therefore it must be provided by all subclasses.
	 */
	public abstract Object executeGeneric(VirtualFrame virtualFrame);
	
	/**
	 * When we use an expression at places where a {@link AstElement statement}
	 * is already sufficient, the return value is just discarded.
	 */
	@Override
	public void executeVoid(VirtualFrame frame) {
		executeGeneric(frame);
	}
	
	@Override public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new AstExpressionWrapper(this, probeNode);
	  }
	
	/*
	 * Execute methods for specialized types. They all follow the same pattern: they
	 * call the generic execution method and then expect a result of their return
	 * type. Type-specialized subclasses overwrite the appropriate methods.
	 */
	
	public byte executeByte(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectByte(this.executeGeneric(virtualFrame));
	}

	public int executeInteger(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectInteger(this.executeGeneric(virtualFrame));
	}

	public long executeLong(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectLong(this.executeGeneric(virtualFrame));
	}

	public float executeFloat(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectFloat(this.executeGeneric(virtualFrame));
	}

	public double executeDouble(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectDouble(this.executeGeneric(virtualFrame));
	}

	public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectBoolean(this.executeGeneric(virtualFrame));
	}

	public char executeChar(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectCharacter(this.executeGeneric(virtualFrame));
	}

	public String executeString(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectString(this.executeGeneric(virtualFrame));
	}
}
