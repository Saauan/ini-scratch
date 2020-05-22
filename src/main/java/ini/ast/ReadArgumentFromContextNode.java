package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

// Reads argument and then write it
public class ReadArgumentFromContextNode extends AstElement implements Expression {
	
	public final int argumentIndex;

	public ReadArgumentFromContextNode(IniParser parser, Token token, int argumentIndex) {
		super(parser, token);
		this.argumentIndex = argumentIndex;
	}

	@Override
	public void accept(Visitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prettyPrint(PrintStream out) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
        if (!this.isArgumentIndexInRange(virtualFrame, this.argumentIndex)) {
            throw new IllegalArgumentException("Not enough arguments passed");
        }
        return this.getArgument(virtualFrame, this.argumentIndex);
	}

    protected boolean isArgumentIndexInRange(VirtualFrame virtualFrame,
            int index) {
        return (index + 1) < virtualFrame.getArguments().length;
    }

    protected Object getArgument(VirtualFrame virtualFrame, int index) {
        return virtualFrame.getArguments()[index + 1];
    }

    protected static MaterializedFrame getLexicalScope(Frame frame) {
        Object[] args = frame.getArguments();
        if (args.length > 0) {
            return (MaterializedFrame) frame.getArguments()[0];
        } else {
            return null;
        }
    }
	
}
