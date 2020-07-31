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

				evalAt.parseInParameters(frame, evalRule.atPredicate.annotations);
				Env env = lookupContextReference(IniLanguage.class).get().getEnv();
				evalAt.executeAndSetEnv(frame, env);
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
