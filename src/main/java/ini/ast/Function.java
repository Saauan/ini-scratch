package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

public class Function extends Executable {

	public Sequence<AstNode> statements;
	public boolean oneExpressionLambda = false;

	@Deprecated
	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstNode> statements) {
		this(parser, token, name, parameters, statements, null);
	}

	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstNode> statements, RootCallTarget callTarget) {
		super(parser, token, name, parameters, callTarget);
		this.statements = statements;
		this.nodeTypeId = AstNode.FUNCTION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (name != null) {
			out.print("function " + name);
		}
		out.print("(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		Sequence<AstNode> s = statements;
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

	/**
	 * Retrieve the function from the function registry
	 */
	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		return null;
	}

}
