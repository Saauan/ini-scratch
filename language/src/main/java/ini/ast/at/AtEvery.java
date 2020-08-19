package ini.ast.at;

import java.io.PrintStream;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniContext;
import ini.IniLanguage;

public class AtEvery extends At {

	Thread mainThread;

	@Override
	public void executeVoid(VirtualFrame frame) {
//		ruleThread = this.env.createThread(new IniThread(this, getRule(), frame), this.env.getContext());
		At thisAt = this;
		Env env = lookupContextReference(IniLanguage.class).get().getEnv();
		IniContext context = lookupContextReference(IniLanguage.class).get();
		mainThread = env.createThread(new Thread() {
			@Override
			public void run() {
				do {
					try {
						sleep((int) getInContext().get("time")); // BUG : Cast to int, may have issues later
					} catch (InterruptedException e) {
						break;
					}
					createAndRunRuleThread(frame, thisAt, env, context);
				} while (!checkTerminated());
				System.err.println("Thread terminated");
			}
		}, env.getContext());
		context.startedThreads.add(mainThread);
		mainThread.start();
	}

	@Override
	public void prettyPrint(PrintStream out) {}
	
}
