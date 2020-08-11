package ini.runtime;

import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.ast.AstElement;
import ini.ast.Rule;
import ini.ast.at.At;

public class IniThread extends Thread {

	public final AstElement toEval;
	public String atName;
	static int threadCount = 1;
	private At at;
	private MaterializedFrame frame;

	public IniThread(At at, AstElement toEval, VirtualFrame frame) {
		this.toEval = toEval;
		this.at = at;
		// We need to materialize the frame to store it in a field
		this.frame = frame.materialize();
		if ((toEval instanceof Rule) && ((Rule) toEval).atPredicate != null) {
			this.setName(((Rule) toEval).atPredicate.toString() + ":" + threadCount++);
		}
	}

	@Override
	public void run() {
		if (at != null) {
			at.safelyEnter();
		}
		try {
			toEval.executeVoid(frame);
		} catch (IniException e) {
			try {
				// Delegate to the process runner
				at.processRunner.handleException(frame, e);
			} catch (RuntimeException re) {
				System.err.println(e);
				e.printStackTrace(System.err);
			}
		} finally {
			if (at != null) {
				at.popThread();
			}
		}
	}

	public void kill() {
		this.setName(this.getName() + ":killed");
	}

}
