package ini.ast.at;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.ast.Assignment;
import ini.ast.AstElement;
import ini.ast.AstExpression;
import ini.ast.AtPredicate;
import ini.ast.Expression;
import ini.ast.Process;
import ini.ast.Rule;
import ini.ast.Variable;

public abstract class At extends AstElement{
	protected boolean terminated = false;
	/* TODO : Useful ? */
	public static Map<String, Class<? extends At>> atPredicates = new HashMap<String, Class<? extends At>>();
	/* TODO : Useful ? */
	public List<At> synchronizedAts = new ArrayList<At>();
	private ThreadPoolExecutor threadExecutor;
	private int currentThreadCount = 0;
	private int currentThreadCountInQueue = 0;
	private Map<String, Object> inContext = new HashMap<String, Object>();
	private Rule rule;
	private AtPredicate atPredicate;
	public Process process;
	/* TODO : Never changed, useful ? */
	private boolean async = false;
	protected Env env;

	public boolean isAsync() {
		return async;
	}

	public static boolean checkAllTerminated(List<At> ats) {
		if (ats == null)
			return true;
		for (At at : ats) {
			if (!at.checkTerminated()) {
				return false;
			}
		}
		return true;
	}

	public static void destroyAll(List<At> ats) {
		if (ats == null)
			return;
		for (At at : ats) {
			at.destroy();
		}
	}
	
	public static String getFrameSlotIdentifier(String identifier) {
		return "at : " + identifier;
	}

	static int currentId = 1;
	int id;
	static {
//		atPredicates.put("update", AtUpdate.class);
		atPredicates.put("every", AtEvery.class);
//		atPredicates.put("cron", AtCron.class);
//		atPredicates.put("read_keyboard", AtReadKeyboard.class);
//		atPredicates.put("consume", AtConsume.class);
	}
	
	public void executeAndSetEnv(VirtualFrame frame, Env env) {
		this.env = env;
		this.executeVoid(frame);
	}

	public At() {
		id = currentId++;
	}

	public int getId() {
		return id;
	}

	public boolean checkTerminated() {
		return terminated;
	}

	synchronized public void terminate() {
		terminated = true;
		// we stop the executor in another thread in case we are in the thread of
		// a running task that would prevent proper shutdown
		this.env.createThread(new Thread() {
			public void run() {
				if (threadExecutor != null) {
					threadExecutor.shutdown();
					try {
						while (!threadExecutor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, this.env.newContextBuilder().build()).start();
	}

	public void restart(VirtualFrame frame, Env env) {
		terminated = false;
		threadExecutor = null;
		executeVoid(frame);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "@" + hashCode() + "-" + (async ? "async" : "sync");
	}

	public void executeThread(Thread thread) {
		pushThreadInQueue();
		if (async) {
			getThreadExecutor().execute(null);
		} else {
			thread.run();
		}
	}

	public ThreadPoolExecutor getThreadExecutor() {
		if (threadExecutor == null) {
			threadExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			threadExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					// System.out.println("REJECTED");
				}
			});
		}
		return threadExecutor;
	}

	public void destroy() {
		// we stop the executor in another thread in case we are in the tread of
		// a running task that would prevent proper shutdown
		new Thread() {
			public void run() {
				if (threadExecutor != null) {
					if (threadExecutor != null && !threadExecutor.isShutdown()) {
						threadExecutor.shutdownNow();
						// try {
						// threadExecutor.shutdown();
						// while (!threadExecutor.awaitTermination(100,
						// TimeUnit.MILLISECONDS)) {
						//
						// }
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					}
				}
			}
		}.start();
	}

	synchronized private void pushThread() {
		// System.out.println("enter " + this);
		currentThreadCount++;
		// System.out.println("push: " + this + "," + currentThreadCount);
	}

	synchronized private void pushThreadInQueue() {
		// System.out.println("enter " + this);
		currentThreadCountInQueue++;
		// System.out.println("push: " + this + "," + currentThreadCount);
	}

	public synchronized void popThread() {
		// System.out.println("exit " + this);
		currentThreadCount--;
		currentThreadCountInQueue--;
		// Main.LOGGER.debug("ended thread " + this + " (active thread count=" +
		// currentThreadCount + ")");
		// System.out.println("pop: " + this + "," + currentThreadCount);
		notifyAll();
	}

	private synchronized void isEmpty() {
		while (currentThreadCount > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected synchronized void isEmptyQueue() {
		while (currentThreadCountInQueue > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	synchronized private void isEmptySynchronizedAts() {
		// System.out.println("all ats: " + synchronizedAts);
		for (At at : synchronizedAts) {
			at.isEmpty();
		}
	}

	// Object monitor = new Object();

	synchronized public void safelyEnter() {
		// System.out.println("safely enter 1 " + this + " >>>");
		// synchronized (monitor) {
		// System.out.println("safely enter 2 " + this + " >>>");
		isEmptySynchronizedAts();
		pushThread();
		// this.notifyAll();
		// }
		// System.out.println("safely enter 3 " + this + " >>>");
	}

	public void parseInParameters(VirtualFrame frame, List<Expression> inParameters) {
		if (inParameters == null) {
			return;
		}
		for (Expression e : inParameters) {
			Assignment a = (Assignment) e;
			AstExpression expr;
			try {
				expr = (AstExpression) FieldUtils.readField(a, "assignmentValue_", true);
				inContext.put(((Variable) a.assignee).name, expr.executeGeneric(frame));
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
	}

	public Map<String, Object> getInContext() {
		return inContext;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public AtPredicate getAtPredicate() {
		return atPredicate;
	}

	public void setAtPredicate(AtPredicate atPredicate) {
		this.async = "async".equals(atPredicate.getAnnotationStringValue("mode"));
		this.atPredicate = atPredicate;
	}

}
