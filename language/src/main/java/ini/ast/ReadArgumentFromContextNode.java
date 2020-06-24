package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

/**
 * Reads an argument from the frame's argument at a specific index and returns it
 * 
 *
 */
public class ReadArgumentFromContextNode extends AstExpression implements Expression {
	
	public final int argumentIndex;

	public ReadArgumentFromContextNode(IniParser parser, Token token, int argumentIndex) {
		super(parser, token);
		this.argumentIndex = argumentIndex;
	}

	@Override
	public void accept(Visitor visitor) {
	}

	@Override
	public void prettyPrint(PrintStream out) {
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
        if (!this.isArgumentIndexInRange(virtualFrame, this.argumentIndex)) {
            throw new IllegalArgumentException("Not enough arguments passed in the frame");
        }
        return this.getArgumentAtIndex(virtualFrame, this.argumentIndex);
	}

    protected boolean isArgumentIndexInRange(VirtualFrame virtualFrame,
            int index) {
        return (index) < virtualFrame.getArguments().length;
    }

    protected Object getArgumentAtIndex(VirtualFrame virtualFrame, int index) {
        return virtualFrame.getArguments()[index];
    }
}
