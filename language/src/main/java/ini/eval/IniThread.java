package ini.eval;

import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.IniRootNode;
import ini.ast.Rule;
import ini.eval.at.At;
import ini.runtime.IniException;

public class IniThread extends Thread {

	public final AstElement toEval;
	public String atName;
	static int threadCount = 1;
	private At at;
	private VirtualFrame frame;

	public IniThread(At at, AstElement toEval, VirtualFrame frame) {
		this.toEval = toEval;
		this.at = at;
		this.frame = frame;
		if ((toEval instanceof Rule) && ((Rule) toEval).atPredicate != null) {
			this.setName(((Rule) toEval).atPredicate.toString() + ":" + threadCount++);
		}
	}

	public IniThread fork() {
		IniThread forked = new IniThread(at, toEval, frame);
		return forked;
	}

	@Override
	public void run() {
		if (at != null) {
			at.safelyEnter();
		}
		try {
			// Copy the variables passed at instanciation
;
			toEval.executeVoid(frame);;
		} catch (IniException e) {
			try {
				at.process.handleException(frame, e);
			} catch (RuntimeException re) {
				System.err.println(e);
				e.printStackTrace(System.err);
			}
		} finally {
			if (at != null) {
				IniLanguage.LOGGER.debug("end: " + at);
				// System.out.println("------pop: " + at);
				at.popThread();
			}
		}
	}

	public void kill() {
//		child.kill = true;
		this.setName(this.getName() + ":killed");
	}

}
