package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniLanguage;
import ini.parser.IniParser;
import ini.runtime.IniFunction;

@NodeInfo(shortName="function", description="builds and contain a IniFunction")
public class Function extends Executable {

	private final IniFunction function;
	public Sequence<AstElement> statements;
	public boolean oneExpressionLambda = false;
	private boolean scopeSet = false;

	@Deprecated
	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstElement> statements) {
		this(parser, token, name, parameters, statements, null, null);
	}

	@Deprecated
	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstElement> statements, FrameDescriptor frameDescriptor, IniLanguage lang) {
		super(parser, token, name, parameters);
		this.statements = statements;
		this.nodeTypeId = AstNode.FUNCTION;
		FrameSlot[] parametersSlots = convertListToFrameSlot(parameters, frameDescriptor);
		this.function = IniFunction.create(lang, parametersSlots, statements, frameDescriptor);
	}
	
	private static FrameSlot[] convertListToFrameSlot(List<Parameter> parameters, FrameDescriptor frameDescriptor) {
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
	

    @Specialization
    public Object getIniFunction(VirtualFrame virtualFrame) {
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
		Sequence<AstElement> s = statements;
		while (s != null) {
			s.get().prettyPrint(out);
			out.println();
			s = s.next();
		}
		out.println("}");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitFunction(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
}
