package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniLanguage;
import ini.ast.at.At;
import ini.runtime.IniException;

public class Process extends Executable {

	@Children
	public Rule[] initRules = new Rule[0];
	@Children
	public Rule[] atRules = new Rule[0];
	@Children
	public Rule[] rules = new Rule[0];
	@Children
	public Rule[] endRules = new Rule[0];
	@Children
	public Rule[] readyRules = new Rule[0];
	@Children
	public Rule[] errorRules = new Rule[0];

	public Process(String name, List<Parameter> parameters, List<Rule> rules) {
		super(name, parameters);
		for (Rule r : new ArrayList<Rule>(rules)) {
			if (r.atPredicate != null) {
				switch (r.atPredicate.kind) {
				case INIT:
					initRules = ArrayUtils.add(initRules, r);
					break;
				case END:
					endRules = ArrayUtils.add(endRules, r);
					break;
				case READY:
					readyRules = ArrayUtils.add(readyRules, r);
					break;
				case ERROR:
					errorRules = ArrayUtils.add(errorRules, r);
					break;
				default:
					atRules = ArrayUtils.add(atRules, r);
				}
			} else {
				this.rules = ArrayUtils.add(this.rules, r);
			}
		}
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		out.print("process " + name + "(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		for (Rule r : initRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : rules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : atRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : readyRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : endRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : errorRules) {
			r.prettyPrint(out);
			out.println();
		}
		out.println("}");
	}

	@Override
	public String toString() {
		return "process " + super.toString();
	}

//	@Override
//	public void eval(IniEval eval) {
//		List<At> ats = null;
//		try {
//			for (Rule rule : this.initRules) {
//				eval.eval(rule);
//			}
//			if (!this.atRules.isEmpty()) {
//				ats = new ArrayList<At>();
//			}
//			Map<Rule, At> atMap = new HashMap<Rule, At>();
//			for (Rule rule : this.atRules) {
//				// At at = At.atPredicates.get(rule.atPredicate.name);
//				Class<? extends At> c = At.atPredicates.get(rule.atPredicate.name);
//				At at = null;
//				try {
//					at = c.newInstance();
//					at.setRule(rule);
//					at.process = this;
//					at.setAtPredicate(rule.atPredicate);
//					ats.add(at);
//					if (rule.atPredicate.identifier != null) {
//						eval.invocationStack.peek().bind(rule.atPredicate.identifier, new RawData(at));
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (at == null) {
//					throw new RuntimeException("unknown @ predicate '" + rule.atPredicate.name + "'");
//				}
//				atMap.put(rule, at);
//			}
//			Iterator<Rule> itr = atMap.keySet().iterator();
//			while (itr.hasNext()) {
//				Rule evalRule = itr.next();
//				At evalAt = atMap.get(evalRule);
//				List<Expression> synchronizedAtsNames = evalRule.synchronizedAtsNames;
//				if (synchronizedAtsNames != null) {
//					for (Expression e : synchronizedAtsNames) {
//						evalAt.synchronizedAts.add((At) eval.eval(e).getValue());
//					}
//				}
//
//				eval.evaluationStack.push(evalRule.atPredicate);
//				evalAt.parseInParameters(eval, evalRule.atPredicate.annotations);
//				evalAt.eval(eval);
//				eval.evaluationStack.pop();
//			}
//			onReady(eval);
//			do {
//				eval.invocationStack.peek().noRulesApplied = false;
//				while (!eval.invocationStack.peek().noRulesApplied) {
//					eval.invocationStack.peek().noRulesApplied = true;
//					for (Rule rule : this.rules) {
//						eval.eval(rule);
//					}
//				}
//			} while (!At.checkAllTerminated(ats));
//			At.destroyAll(ats);
//			for (Rule rule : this.endRules) {
//				eval.eval(rule);
//			}
//			// unlocks waiting invokers
//			Context ctx = eval.invocationStack.peek();
//			Data r = ctx.get(IniEval.PROCESS_RESULT);
//			if (r != null) {
//				r.copyData(new RawData());
//			}
//		} catch (ReturnException e) {
//			// swallow
//		} catch (RuntimeException e) {
//			handleException(eval, e);
//		} /*
//			 * finally { //At.destroyAll(ats); }
//			 */
//
//	}
//
//	private void onReady(IniEval eval) {
//		for (Rule rule : this.readyRules) {
//			if (rule.guard == null || eval.eval(rule.guard).isTrueOrDefined()) {
//				Sequence<Statement> s = rule.statements;
//				while (s != null) {
//					eval.eval(s.get());
//					s = s.next();
//				}
//			}
//		}
//	}
//
//	public void handleException(IniEval eval, RuntimeException e) throws RuntimeException {
//		boolean caught = false;
//		for (Rule rule : this.errorRules) {
//			if (rule.guard == null || eval.eval(rule.guard).isTrueOrDefined()) {
//				eval.invocationStack.peek().bind(((Variable) rule.atPredicate.outParameters.get(0)).name,
//						new RawData(e));
//				Sequence<Statement> s = rule.statements;
//				while (s != null) {
//					eval.eval(s.get());
//					s = s.next();
//				}
//				caught = true;
//			}
//		}
//		if (!caught) {
//			// unlocks waiting invokers
//			Context ctx = eval.invocationStack.peek();
//			Data r = ctx.get(IniEval.PROCESS_RESULT);
//			if (r != null) {
//				r.copyData(new RawData());
//			}
//			throw e;
//		}
//
//	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitProcess(this);
	}

	/**
	 * Creates and initialize the process
	 */
	@Override
	public Object executeGeneric(VirtualFrame frame) {
		List<At> ats = null;
		try {
			// Execute the init rules
			for (Rule rule : this.initRules) {
				rule.executeVoid(frame);
			}

			// Set up all the at related rules | I don't understand this part
			if (this.atRules.length > 0) {
				ats = new ArrayList<At>();
			}
			Map<Rule, At> atMap = new HashMap<Rule, At>();
			for (Rule rule : this.atRules) {
				At at = rule.atPredicate.attachedAt;
				if (at == null) {
					throw new RuntimeException("unknown @ predicate '" + rule.atPredicate.name + "'");
				}
				/* Initialize the At */
				at.setRule(rule);
				at.process=this;
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

//				eval.evaluationStack.push(evalRule.atPredicate);
				evalAt.parseInParameters(frame, evalRule.atPredicate.annotations);
				Env env = lookupContextReference(IniLanguage.class).get().getEnv();
				evalAt.executeAndSetEnv(frame, env);
//				eval.evaluationStack.pop();
			}

			// Execute all the readyRules
			for (Rule rule : this.readyRules) {
				rule.executeVoid(frame);
			}

			// While the rules are not terminated and can be executed, execute them in order
			do {
				boolean atLeastOneRuleExecuted = true;
				while (atLeastOneRuleExecuted) {
					atLeastOneRuleExecuted = false;
					for (Rule rule : this.rules) {
						atLeastOneRuleExecuted = rule.executeBoolean(frame) ? true : atLeastOneRuleExecuted;
					}
				}
			} while (!At.checkAllTerminated(ats));
			
			// Destroy the ats
			At.destroyAll(ats);
			
			// Execute the end rules
			for (Rule rule : this.endRules) {
				rule.executeBoolean(frame);
			}
		} catch (IniException e) {
			handleException(frame, e);
		} catch (ReturnException r) {
			At.destroyAll(ats);
			
			throw r;
		}
		return null;

	}

	/**
	 * Adds the At to the frame using its identifier.
	 */
	private void addAtToFrame(VirtualFrame frame, String identifier, At at) {
//		String frameSlotIdentifier = At.getFrameSlotIdentifier(identifier);
		FrameSlot slot = frame.getFrameDescriptor().findOrAddFrameSlot(identifier);
		frame.setObject(slot, at);
	}

	public void handleException(VirtualFrame frame, IniException e) throws RuntimeException {
		boolean caught = false;
		for (Rule rule : this.errorRules) {
			caught = rule.executeBoolean(frame) ? true : caught;
		}
		throw e; 
	}

}
