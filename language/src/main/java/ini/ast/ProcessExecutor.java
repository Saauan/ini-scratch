package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;

import ini.IniContext;
import ini.IniLanguage;
import ini.ast.at.At;
import ini.runtime.IniException;

/* That is the class that will be called to execute the process 
 * Wraps a Process Node so as to access its rules */
@GenerateWrapper
public class ProcessExecutor extends AstExpression implements Runnable, InstrumentableNode {
	
	@Child
	private Process wrappedProcess;
	
	MaterializedFrame frame = null;
	
	public ProcessExecutor(Process wrappedProcess) {
		this.wrappedProcess = wrappedProcess;
	}
	
	/*
	 * This constructor is necessary for @GenerateWrapper to work */
	public ProcessExecutor() {}


	@Override
	public void run() {
		assert frame != null: "frame has not been initialized";
		List<At> ats = null;
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
				at.processExecutor=this;
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
				Env env = lookupContextReference(IniLanguage.class).get().getEnv();
				evalAt.executeAndSetEnv(frame, env);
			}

			// Execute all the readyRules
			for (Rule rule : this.wrappedProcess.readyRules) {
				rule.executeVoid(frame);
			}

			// While the rules are not terminated and can be executed, execute them in order
			do {
				boolean atLeastOneRuleExecuted = true;
				while (atLeastOneRuleExecuted) {
					atLeastOneRuleExecuted = false;
					for (Rule rule : this.wrappedProcess.rules) {
						atLeastOneRuleExecuted = rule.executeBoolean(frame) ? true : atLeastOneRuleExecuted;
					}
				}
			} while (!At.checkAllTerminated(ats));
			
			// Destroy the ats
			At.destroyAll(ats);
			
			// Execute the end rules
			for (Rule rule : this.wrappedProcess.endRules) {
				rule.executeBoolean(frame);
			}
		} catch (IniException e) {
			handleException(frame, e);
		} catch (ReturnException r) {
			At.destroyAll(ats);
			
			throw r;
		}
	}
	
	@Override
	public Object executeGeneric(VirtualFrame frame) {
		this.frame = frame.materialize();
		Env env = lookupContextReference(IniLanguage.class).get().getEnv();
		Thread processThread = env.createThread(this, env.getContext());
		IniContext context = lookupContextReference(IniLanguage.class).get(); 
		context.startedThreads.add(processThread);
		processThread.start();
		return null;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		// TODO Auto-generated method stub
		
	}
	

	/**
	 * Adds the At to the frame using its identifier.
	 */
	private void addAtToFrame(VirtualFrame frame, String identifier, At at) {
		FrameSlot slot = frame.getFrameDescriptor().findOrAddFrameSlot(identifier);
		frame.setObject(slot, at);
	}

	public void handleException(VirtualFrame frame, IniException e) throws RuntimeException {
		boolean caught = false;
		for (Rule rule : this.wrappedProcess.errorRules) {
			caught = rule.executeBoolean(frame) ? true : caught;
		}
		throw e;
	}
	
	@Override public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new ProcessExecutorWrapper(this, probeNode);
	  }


}
