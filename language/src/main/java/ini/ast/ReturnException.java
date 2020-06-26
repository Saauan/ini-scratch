package ini.ast;

import com.oracle.truffle.api.nodes.ControlFlowException;

/*
 * Exception that is thrown when a return statement is attained.
 */
public class ReturnException extends ControlFlowException {

	private static final long serialVersionUID = 1448298462880155212L;

	public final Object result;
	
	public ReturnException(Object result) {
		this.result = result;
	}
	
    public Object getResult() {
        return result;
    }



}
