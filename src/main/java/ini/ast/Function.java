package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

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
	private static FrameSlot[] convertListToFrameSlotArray(List<Parameter> parameters,
			FrameDescriptor frameDescriptor) {
		FrameSlot[] result = new FrameSlot[parameters.size()];
		int nbParam = parameters.size();
		for (int i = 0; i < nbParam; i++) {
			result[i] = frameDescriptor.addFrameSlot(parameters.get(i).name);
		}
		return result;
	}

	public IniFunction getFunction() {
		return this.function;
	}

	public IniFunction getIniFunction(VirtualFrame virtualFrame) {
		IniFunction function = this.getFunction();
		if (!isScopeSet()) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			function.setLexicalScope(virtualFrame.materialize());
			this.scopeSet = true;
		}
		return function;
	}

	protected boolean isScopeSet() {
		return this.scopeSet;
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
		// TODO : Find a way to pass language (first argument) to create
		this.function = IniFunction.create(null,
				name,
				convertListToFrameSlotArray(parameters, virtualFrame.getFrameDescriptor()),
				statements,
				virtualFrame.getFrameDescriptor());
		
		String identifier = getFunctionIdentifier(this.name, this.parameters.size());
		FrameSlot functionSlot = virtualFrame.getFrameDescriptor().addFrameSlot(identifier); // stores the slot into the
																								// FrameDescriptor
		virtualFrame.setObject(functionSlot, this.function); // stores the function into the frame
		// If it is the root context we set the root context to be the lexical scope
		if(virtualFrame.getArguments().length == 0 || (virtualFrame.getArguments().length >= 1 && virtualFrame.getArguments()[0] == null)) {
			this.function.setLexicalScope(virtualFrame.materialize());
		}
		// Else we set the lexical scope of the calling function to be the lexical scope
		else {
			this.function.setLexicalScope((MaterializedFrame) virtualFrame.getArguments()[0]);
			// If the function is created in the main, we add it to the root context
			if (ini.Utils.isMain(virtualFrame)){
				ini.Utils.findRootContext(virtualFrame).setObject(functionSlot, this.function);
			}
		}
		return function;
	}
}
