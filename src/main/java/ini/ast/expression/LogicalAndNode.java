package ini.ast.expression;

import ini.ast.AstElement;
import ini.ast.Token;
import ini.parser.IniParser;

public final class LogicalAndNode extends ShortCircuitNode {

	public LogicalAndNode(IniParser parser, Token token, AstElement left, AstElement right) {
		super(parser, token, left, right);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean isEvaluateRight(boolean leftValue) {
		return leftValue;
	}

	@Override
	protected boolean execute(boolean leftValue, boolean rightValue) {
		return leftValue && rightValue;
	}

}
