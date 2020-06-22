package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniLanguage;
import ini.Utils;
import ini.parser.IniParser;
import ini.runtime.IniFunction;

@NodeInfo(shortName = "function", description = "builds and contain a IniFunction")
public class Function extends Executable {

	private IniFunction function;
	public AstElement[] statements;
	public boolean oneExpressionLambda = false;
	private boolean scopeSet = false;

	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstElement> statements) {
		super(parser, token, name, parameters);
		this.statements = (AstElement[]) Utils.convertSequenceToArray(statements);
		this.nodeTypeId = AstNode.FUNCTION;
		this.parameters = parameters;
	}

	/**
	 * Converts a list of Parameters to an array of frame slots. The identifier of
	 * the slot is the parameter name
	 */
	@ExplodeLoop
	private static FrameSlot[] convertListToFrameSlotArray(List<Parameter> parameters,
			FrameDescriptor frameDescriptor) {
		FrameSlot[] result = new FrameSlot[parameters.size()];
		final int nbParam = parameters.size();
		CompilerAsserts.partialEvaluationConstant(nbParam);
		for (int i = 0; i < nbParam; i++) {
			result[i] = frameDescriptor.addFrameSlot(parameters.get(i).name);
		}
		return result;
	}

	public IniFunction getFunction() {
		return this.function;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (name != null) {
			out.print("function " + name);
		}
		out.print("(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		out.println("Not implemented yet");
//		AstElement[] s = statements;
//		while (s != null) {
//			s.get().prettyPrint(out);
//			out.println();
//			s = s.next();
//		}
		out.println("}");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitFunction(this);
	}

	/**
	 * Stores the function in the frame using a slot created with the function name
	 * and the number of arguments
	 * 
	 * @return the slot in which the function is stored
	 */
	@Override
	public IniFunction executeGeneric(VirtualFrame virtualFrame) {
		// Each time a new function is created, a new frame descriptor is created
		FrameDescriptor frameDescriptor = new FrameDescriptor();

		// TODO : Find a way to pass language (first argument) to create
		IniFunction function = IniFunction.create(null, name, convertListToFrameSlotArray(parameters, frameDescriptor),
				statements, frameDescriptor);
		this.function = function;

		// If it is the root context we set the root context to be the lexical scope
		if (ini.Utils.isRootContext(virtualFrame)) {
			this.function.setLexicalScope(virtualFrame.materialize());
		}
		// Set the lexical scope of the function to be the lexical scope of the calling function
		else {
			// Note : here the lexical scope is the same as the function frame.
			this.function.setLexicalScope((MaterializedFrame) virtualFrame.getArguments()[0]);
		}
		
		registerFunction(function, getFunctionIdentifier(this.name, this.parameters.size()));
		return function;
	}


	private void registerFunction(IniFunction function, String functionId) {
		lookupContextReference(IniLanguage.class).get().getFunctionRegistry().register(functionId, function);
	}
}
