package ini.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniLanguage;
import ini.ast.AstExpression;
import ini.ast.Process;
import ini.ast.ProcessReturnValue;
import ini.ast.Rule;
import ini.ast.at.At;

public class ProcessRunner implements Runnable {

	/*
	 * Even though is it a Node, returnValue does not need the @Child annotation as
	 * it is not part of the AST, it is merely an address remembered by the
	 * ProcessExecutor so that when the process is over, he can set the return value
	 */
	private ProcessReturnValue returnValue;
	private MaterializedFrame frame;
	private Process wrappedProcess;
	private CountDownLatch endSignal = new CountDownLatch(1);
	public CountDownLatch atHasBeenExecutedSignal;
	private boolean isReactive = true;
	public List<Thread> startedThreads = new ArrayList<Thread>();

	// The number of times the rules have been checked
	private int nbRulesCheck = 0;

	public ProcessRunner(ProcessReturnValue returnValue, MaterializedFrame frame, Process wrappedProcess) {
		this.returnValue = returnValue;
		this.frame = frame;
		this.wrappedProcess = wrappedProcess;
	}
	
	public boolean isReactive() {
		return isReactive;
	}

	@Override
	public void run() {
		assert frame != null : "frame has not been initialized";
		List<At> ats = null;
		Object res = null;
		try {
			// Execute the init rules
			for (Rule rule : this.wrappedProcess.initRules) {
				rule.executeVoid(frame);
			}

			// Set up all the at related rules | I don't understand this part
			if (this.wrappedProcess.atRules.length > 0) {
				ats = new ArrayList<At>();
			}
			Map<Rule, At> atMap = new HashMap<Rule, At>();
			for (Rule rule : this.wrappedProcess.atRules) {
				At at = rule.atPredicate.attachedAt;
				if (at == null) {
					throw new RuntimeException("unknown @ predicate '" + rule.atPredicate.name + "'");
				}
				/* Initialize the At */
				at.setRule(rule);
				at.processRunner = this;
				at.setAtPredicate(rule.atPredicate);
				ats.add(at);
				if (rule.atPredicate.identifier != null) {
					addAtToFrame(frame, rule.atPredicate.identifier, at);
				}
				atMap.put(rule, at);
			}
			Iterator<Rule> itr = atMap.keySet().iterator();
			while (itr.hasNext()) {
				Rule evalRule = itr.next();
				At evalAt = atMap.get(evalRule);
				AstExpression[] synchronizedAtsNames = evalRule.synchronizedAtsNames;
				if (synchronizedAtsNames != null) {
					for (AstExpression e : synchronizedAtsNames) {
						evalAt.synchronizedAts.add((At) e.executeGeneric(frame));
					}
				}

				evalAt.parseInParameters(frame, evalRule.atPredicate.annotations);
				evalAt.executeVoid(frame);
			}

			// Execute all the readyRules
			for (Rule rule : this.wrappedProcess.readyRules) {
				rule.executeVoid(frame);
			}

			executeRulesWhilePossible();
			if (ats != null) {

				// While the rules are not terminated and can be executed, execute them in order
				do {
					/*
					 * For some reason, when executing ini/channels/timer.ini when all child threads
					 * are over, the execution blocks for no reason at
					 * "boolean atLeastOneRuleExecuted = true", it means that the thread seems to be
					 * executing normally, but nothing happens.
					 * 
					 * When blocking, if we pause in the Eclipse debugger and resume, it unblocks If
					 * before this hellish line, we put a print, or a sleep, it works as intended
					 * 
					 * I did not manage to pinpoint where this bug comes from
					 */
					Thread.sleep(10);
					if (isReactive()) {
						atHasBeenExecutedSignal = new CountDownLatch(1);
						atHasBeenExecutedSignal.await();
					}
					executeRulesWhilePossible();
				} while (!At.checkAllTerminated(ats));
			}

			// Destroy the ats
			At.destroyAll(ats);

			// Execute the end rules
			for (Rule rule : this.wrappedProcess.endRules) {
				rule.executeBoolean(frame);
			}
			// Ensures all the threads created in the process are over when the process ends.
			joinAllThreads();
		} catch (IniException e) {
			handleException(frame, e);
		} catch (ReturnException r) {
			At.destroyAll(ats);
			res = r.getResult();
			joinAllThreads();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			interruptAllThreads();
		} finally {
			System.err.println("The rules were checked " + this.nbRulesCheck + " times");
			returnValue.setReturnValue(res);
			endSignal.countDown();
		}
	}

	private void executeRulesWhilePossible() {
		boolean atLeastOneRuleExecuted = true;
		while (atLeastOneRuleExecuted) {
			this.nbRulesCheck ++;
			atLeastOneRuleExecuted = false;
			for (Rule rule : this.wrappedProcess.rules) {
				atLeastOneRuleExecuted = rule.executeBoolean(frame) ? true : atLeastOneRuleExecuted;
			}
		}
	}
	
	/**
	 * Ensures all the threads created in the process are over when the process ends.
	 */
	private synchronized void joinAllThreads() {
		IniLanguage.LOGGER.debug("Joining all threads in process " + this);
		try {
			for (int i = 0; i < startedThreads.size(); i++) {
				Thread threadToJoin = startedThreads.get(i);
				threadToJoin.join();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			interruptAllThreads();
		}
	}
	
	private synchronized void interruptAllThreads() {
		IniLanguage.LOGGER.debug("Interrupting all threads in process " + this);
		for (int i=0; i<startedThreads.size(); i++) {
			Thread threadToJoin = startedThreads.get(i);
			threadToJoin.interrupt();
		}
	}

	public void handleException(VirtualFrame frame, IniException e) throws RuntimeException {
		boolean caught = false;
		for (Rule rule : this.wrappedProcess.errorRules) {
			caught = rule.executeBoolean(frame) ? true : caught;
		}
		throw e;
	}
	
	/**
	 * Adds the At to the frame using its identifier.
	 */
	private void addAtToFrame(VirtualFrame frame, String identifier, At at) {
		FrameSlot slot = frame.getFrameDescriptor().findOrAddFrameSlot(identifier);
		frame.setObject(slot, at);
	}

	public void waitForProcessToEnd() throws InterruptedException {
		this.endSignal.await();
	}
	
	public String toString() {
		return "Process " + this.wrappedProcess.name;
	}
	

}
