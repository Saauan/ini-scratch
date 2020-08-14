package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniLanguage;
import ini.Utils;
import ini.runtime.IniFunction;

@NodeInfo(shortName = "function", description = "builds an IniFunction")
public class Function extends Executable {

	@Children
	public AstElement[] statements;
	public boolean oneExpressionLambda = false;

	public Function(String name, List<Parameter> parameters, Sequence<AstElement> statements) {
		super(name, parameters);
		this.statements = (AstElement[]) Utils.convertSequenceToArray(statements);
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
		AstElement[] s = statements;
		for(AstElement statement : s) {
			statement.prettyPrint(out);
		}
		out.println("}");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitFunction(this);
	}

	/**
	 * Creates an IniFunction, registers it, and returns it.
	 */
	@Override
	public IniFunction executeGeneric(VirtualFrame virtualFrame) {
		/* Each time a new function is created, a new frame descriptor is created */
		FrameDescriptor frameDescriptor = new FrameDescriptor();
		IniFunction function = IniFunction.createStatic(lookupContextReference(IniLanguage.class).get().getLang(), name,
				convertListOfParametersToArrayOfFrameSlot(parameters, frameDescriptor), statements, frameDescriptor);

		/* If we are creating a "normal" function, and not a lambda function, we register it */
		if(this.name != null) {
			registerFunction(function, getExecutableIdentifier(this.name, this.parameters.size()));
		}
		return function;
	}

	/*
	 * Registers the function into the function registry 
	 */
	private void registerFunction(IniFunction function, String functionId) {
		lookupContextReference(IniLanguage.class).get().getFunctionRegistry().register(functionId, function);
	}
}
