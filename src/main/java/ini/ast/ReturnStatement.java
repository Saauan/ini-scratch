package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.parser.IniParser;
import ini.runtime.IniFunction;
/**
 * Implementation of the SL return statement. We need to unwind an unknown number of interpreter
 * frames that are between this {@link ReturnStatement} and the {@link IniFunction} of the
 * method we are exiting. This is done by throwing an {@link ReturnException exception} that is
 * caught by the {@link IniFunction#executeGeneric function body}. The exception transports
 * the return value.
 */
@NodeInfo(shortName = "return", description = "The node implementing a return statement")
public class ReturnStatement extends AstElement implements Statement {

	@Child public AstElement valueNode;
	
	public ReturnStatement(IniParser parser, Token token, AstElement expression) {
		super(parser, token);
		this.valueNode = expression;
		this.nodeTypeId=AstNode.RETURN_STATEMENT;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("return");
		if(valueNode!=null) {
			out.print(" ");
			valueNode.prettyPrint(out);
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitReturnStatement(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
        Object result;
        if (valueNode != null) {
            result = valueNode.executeGeneric(frame);
        } else {
            /*
             * Return statement that was not followed by an expression, so return the SL null value.
             */
            result = null;
        }
        throw new ReturnException(result);
	}
	
}
