package ini.ast.expression;

import java.io.PrintStream;

import ini.ast.AstElement;
import ini.ast.Token;
import ini.parser.IniParser;

public final class LogicalOrNode extends ShortCircuitNode {

	public LogicalOrNode(IniParser parser, Token token, AstElement left, AstElement right) {
		super(parser, token, left, right);
	}

	@Override
	protected boolean isEvaluateRight(boolean leftValue) {
		return !leftValue;
	}

	@Override
	protected boolean execute(boolean leftValue, boolean rightValue) {
		return leftValue || rightValue;
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		out.print(left + "||" + right);
	}
}
