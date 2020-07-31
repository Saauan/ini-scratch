package ini.ast.at;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.eval.IniThread;

public class AtEvery extends At {

	Thread mainThread;
	Thread ruleThread;

	@Override
	public void executeVoid(VirtualFrame frame) {
		ruleThread = this.env.createThread(new IniThread(this, getRule(), frame), this.env.newContextBuilder().build());
		mainThread = this.env.createThread(new Thread() {
			@Override
			public void run() {
				do {
					try {
						sleep((int) getInContext().get("time")); // BUG : Cast to int, may have issues later
					} catch (InterruptedException e) {
						break;
					}
					executeThread(ruleThread);
				} while (!checkTerminated());
				System.err.println("Thread terminated");
			}
		}, this.env.newContextBuilder().build());
		mainThread.start();
	}

	@Override
	public void prettyPrint(PrintStream out) {
		// TODO Auto-generated method stub
	}
	
	
}
