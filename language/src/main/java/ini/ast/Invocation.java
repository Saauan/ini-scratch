package ini.ast;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import ini.IniLanguage;
import ini.parser.IniParser;
import ini.runtime.IniFunction;

/**
 * Invocations share the same name as the function or process they invoke
 * 
 * If the function is not found within the current context, searchs in the root
 * context
 */
public class Invocation extends AstExpression implements Statement, Expression {

	@Children
	public AstExpression[] argumentNodes;
	@Child
	protected IndirectCallNode callNode;
	public String name;

	public Invocation(IniParser parser, Token token, String name, List<Expression> arguments) {
		super(parser, token);
		this.name = name;
		this.nodeTypeId = AstNode.INVOCATION;
		this.argumentNodes = arguments.toArray(new AstExpression[0]);
		this.callNode = Truffle.getRuntime().createIndirectCallNode();
	}

	@Override
	public String toString() {
		return name + "(" + StringUtils.join(argumentNodes, ",") + ")";
	}

	@Override
	@Deprecated
	public void prettyPrint(PrintStream out) {
//		out.print(name + "(");
//		prettyPrintList(out, argumentsNodes, ",");
//		out.print(")");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitInvocation(this);
	}

	@Override
	@ExplodeLoop
	public Object executeGeneric(VirtualFrame virtualFrame) {
		IniFunction function = this.lookupFunction(getFunctionIdentifier(this.name, this.argumentNodes.length));
		if (function == null) {
			throw new RuntimeException(String.format("The function %s was not found", this.name));
		}
		/*
		 * The number of arguments is constant for one invoke node. During compilation,
		 * the loop is unrolled and the execute methods of all arguments are inlined.
		 * This is triggered by the ExplodeLoop annotation on the method. The compiler
		 * assertion below illustrates that the array length is really constant.
		 */
		final int nbArguments = this.argumentNodes.length;
		CompilerAsserts.partialEvaluationConstant(nbArguments);

		Object[] argumentValues = new Object[nbArguments + 1];
		// The first element of the frame's argument is the lexical scope
		argumentValues[0] = function.getLexicalScope();
		assert function.getLexicalScope() != null : String.format("The lexical scope of the function %s was null",
				function.name);
		for (int i = 0; i < nbArguments; i++) {
			argumentValues[i + 1] = this.argumentNodes[i].executeGeneric(virtualFrame);
		}

		return call(virtualFrame, function.callTarget, argumentValues);
	}

	public IniFunction lookupFunction(String functionId) {
		IniFunction function = null;
		function = lookupContextReference(IniLanguage.class).get().getFunctionRegistry().lookup(functionId);
		return function;
	}

	protected Object call(VirtualFrame virtualFrame, CallTarget callTarget, Object[] arguments) {
		return this.callNode.call(callTarget, arguments);
	}
}
