package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniFunction;
/**
 * Implementation of the INI return statement. We need to unwind an unknown number of interpreter
 * frames that are between this {@link ReturnStatement} and the {@link IniFunction} of the
 * method we are exiting. This is done by throwing an {@link ReturnException exception} that is
 * caught by the {@link IniFunction#executeGeneric function body}. The exception transports
 * the return value.
 */
@NodeInfo(shortName = "return", description = "The node implementing a return statement")
public class ReturnStatement extends AstElement implements Statement {

	@Child public AstExpression valueNode;
	
	public ReturnStatement(AstExpression expression) {
		super();
		this.valueNode = expression;
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
	public void executeVoid(VirtualFrame frame) {
        Object result;
        if (valueNode != null) {
            result = valueNode.executeGeneric(frame);
        } else {
            /*
             * Return statement that was not followed by an expression, so return the null value.
             */
            result = null;
        }
        throw new ReturnException(result);
	}
	
}
