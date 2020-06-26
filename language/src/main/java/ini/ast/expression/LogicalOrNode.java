package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.AstExpression;

@NodeInfo(shortName = "||")
public final class LogicalOrNode extends ShortCircuitNode {

	public LogicalOrNode(AstExpression left, AstExpression right) {
		super(left, right);
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
