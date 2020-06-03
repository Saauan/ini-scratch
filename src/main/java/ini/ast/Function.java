package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniLanguage;
import ini.parser.IniParser;
import ini.runtime.IniFunction;

@NodeInfo(shortName="function", description="builds and contain a IniFunction")
public class Function extends Executable {

	private IniFunction function;
	private final FrameSlot[] parametersSlots;
	public AstElement[] statements;
	public boolean oneExpressionLambda = false;
	private boolean scopeSet = false;
	
	private FrameDescriptor frameDescriptor;
	private IniLanguage lang;

	@Deprecated
	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstElement> statements) {
		this(parser, token, name, parameters, statements, null, null);
	}

	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstElement> statements, FrameDescriptor frameDescriptor, IniLanguage lang) {
		super(parser, token, name, parameters);
		this.statements = convertSequenceToArray(statements);
		this.nodeTypeId = AstNode.FUNCTION;
		this.parametersSlots = convertListToFrameSlotArray(parameters, frameDescriptor);
		
		this.frameDescriptor = frameDescriptor;
		this.lang = lang;
	}
	
	// TODO : Unfold loop and compilation final
	private static AstElement[] convertSequenceToArray(Sequence<AstElement> statements) {
		final int nbStatements = statements.size();
		AstElement[] res = new AstElement[nbStatements];
		for (int i = 0; i < nbStatements; i++) {
			res[i] = statements.get();
			statements = statements.next();
		}
		return res;
	}
	
	/**
	 * Converts a list of Parameters to an array of frame slots. The identifier of the slot is the parameter name
	 */
	private static FrameSlot[] convertListToFrameSlotArray(List<Parameter> parameters, FrameDescriptor frameDescriptor) {
		FrameSlot[] result = new FrameSlot[parameters.size()];
		int nbParam = parameters.size();
		for(int i=0; i<nbParam; i++) {
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
		if(name != null) {
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
	 * Stores the function in the frame using a slot created with the function name and the number of arguments
	 * 
	 * @return the slot in which the function is stored
	 */
	@Override
	public IniFunction executeGeneric(VirtualFrame virtualFrame) {
		this.function = IniFunction.create(lang, parametersSlots, statements, frameDescriptor); // creates the function
		this.function.setLexicalScope(virtualFrame.materialize());
		String identifier = getFunctionIdentifier(this.name, this.parametersSlots.length);
		FrameSlot functionSlot = virtualFrame.getFrameDescriptor().addFrameSlot(identifier); // stores the slot into the FrameDescriptor
		virtualFrame.setObject(functionSlot, this.function); // stores the function into the frame
		return function;
	}
}
