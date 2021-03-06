package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * Reads an argument from the frame's argument at a specific index and returns
 * it
 * 
 * It is used by IniRootNode when creating a new function
 */
public class ReadArgumentFromContextNode extends AstExpression implements Expression {

	/* The index of the argument in the frame's arguments*/
	public final int argumentIndex;

	public ReadArgumentFromContextNode(int argumentIndex) {
		super();
		this.argumentIndex = argumentIndex;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitReadArgumentFromContextNode(this);
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("arg at index : " + argumentIndex);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		if (!this.isArgumentIndexInRange(virtualFrame, this.argumentIndex)) {
			throw new IllegalArgumentException("Not enough arguments passed in the frame");
		}
		return this.getArgumentAtIndex(virtualFrame, this.argumentIndex);
	}

	protected boolean isArgumentIndexInRange(VirtualFrame virtualFrame, int index) {
		return (index) < virtualFrame.getArguments().length;
	}

	protected Object getArgumentAtIndex(VirtualFrame virtualFrame, int index) {
		return virtualFrame.getArguments()[index];
	}
}
