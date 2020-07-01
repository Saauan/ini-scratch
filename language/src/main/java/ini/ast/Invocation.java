package ini.ast;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import ini.IniContext;
import ini.IniLanguage;
import ini.runtime.IniException;
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
	@Child public IndirectCallNode callNode;
	public String name;
	
    /**
     * The resolved function. During parsing (in the constructor of this node), we do not have the
     * {@link IniContext} available yet, so the lookup can only be done at {@link #executeGeneric
     * first execution}. The {@link CompilationFinal} annotation ensures that the function can still
     * be constant folded during compilation.
     */
	@CompilationFinal private IniFunction cachedFunction;

	public Invocation(String name, List<AstExpression> arguments) {
		super();
		this.name = name;
		this.argumentNodes = arguments != null ? arguments.toArray(new AstExpression[0]) : new AstExpression[0];
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
		if (cachedFunction == null) {
			/* We are about to change a @CompilationFinal field. */
			CompilerDirectives.transferToInterpreterAndInvalidate();
			/* First execution of the node: lookup the function in the function registry. */
			cachedFunction = this.lookupFunction(getFunctionIdentifier(this.name, this.argumentNodes.length));
			if (cachedFunction == null) {
				throw new IniException(String.format("The function %s was not found", this.name), this);
			}
		}

		/*
		 * The number of arguments is constant for one invoke node. During compilation,
		 * the loop is unrolled and the execute methods of all arguments are inlined.
		 * This is triggered by the ExplodeLoop annotation on the method. The compiler
		 * assertion below illustrates that the array length is really constant.
		 */
		final int nbArguments = this.argumentNodes.length;
		CompilerAsserts.partialEvaluationConstant(nbArguments);

		Object[] argumentValues = new Object[nbArguments];
		for (int i = 0; i < nbArguments; i++) {
			argumentValues[i] = this.argumentNodes[i].executeGeneric(virtualFrame);
		}
		
		return this.callNode.call(cachedFunction.callTarget, argumentValues);
	}

	public IniFunction lookupFunction(String functionId) {
		IniFunction function = null;
		function = lookupContextReference(IniLanguage.class).get().getFunctionRegistry().lookup(functionId);
		return function;
	}
}
