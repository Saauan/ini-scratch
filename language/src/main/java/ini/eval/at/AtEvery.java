package ini.eval.at;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.eval.IniThread;

public class AtEvery extends At {

	Thread mainThread;
	Thread ruleThread;

	@Override
	public void execute(VirtualFrame frame) {
		ruleThread = this.env.createThread(new IniThread(this, getRule(), frame));
		mainThread = new Thread() {
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
		};
		mainThread.start();
	}
	
	
}
