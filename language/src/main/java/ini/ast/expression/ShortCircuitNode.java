package ini.ast.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import ini.ast.AstElement;
import ini.ast.AstExpression;
import ini.ast.Token;
import ini.parser.IniParser;
import ini.runtime.IniException;

public abstract class ShortCircuitNode extends AstExpression {

	@Child
	protected AstExpression left;
	@Child
	protected AstExpression right;

	/**
	 * Short circuits might be used just like a conditional statement it makes sense
	 * to profile the branch probability.
	 */
	private final ConditionProfile evaluateRightProfile = ConditionProfile.createCountingProfile();

	public ShortCircuitNode(IniParser parser, Token token, AstExpression left, AstExpression right) {
		super(parser, token);
		this.left = left;
		this.right = right;
	}

	@Override
	public final Object executeGeneric(VirtualFrame frame) {
		return executeBoolean(frame);
	}

	@Override
	public final boolean executeBoolean(VirtualFrame frame) {
		boolean leftValue;
		try {
			leftValue = left.executeBoolean(frame);
		} catch (UnexpectedResultException e) {
			throw IniException.typeError(this, e.getResult(), null);
		}
		boolean rightValue;
		try {
			if (evaluateRightProfile.profile(isEvaluateRight(leftValue))) {
				rightValue = right.executeBoolean(frame);
			} else {
				rightValue = false;
			}
		} catch (UnexpectedResultException e) {
			throw IniException.typeError(this, leftValue, e.getResult());
		}
		return execute(leftValue, rightValue);
	}

	protected abstract boolean isEvaluateRight(boolean leftValue);

	protected abstract boolean execute(boolean leftValue, boolean rightValue);
}
