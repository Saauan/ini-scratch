package ini.ast.at;

import java.io.PrintStream;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniContext;
import ini.IniLanguage;
import ini.runtime.IniThread;

public class AtEvery extends At {

	Thread mainThread;
	Thread ruleThread;

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
					ruleThread = env.createThread(new IniThread(thisAt, getRule(), frame), env.getContext());
					runThread(ruleThread);
					context.startedThreads.add(ruleThread);
				} while (!checkTerminated());
				System.err.println("Thread terminated");
			}
		}, env.getContext());
//		context.startedThreads.add(ruleThread);
		context.startedThreads.add(mainThread);
		mainThread.start();
	}

	@Override
	public void prettyPrint(PrintStream out) {}
	
	
}
