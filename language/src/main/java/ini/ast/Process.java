package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Process extends Executable {

	public List<Rule> initRules = new ArrayList<Rule>();
	public List<Rule> atRules = new ArrayList<Rule>();
	public List<Rule> rules = new ArrayList<Rule>();
	public List<Rule> endRules = new ArrayList<Rule>();
	public List<Rule> readyRules = new ArrayList<Rule>();
	public List<Rule> errorRules = new ArrayList<Rule>();

	public Process(String name, List<Parameter> parameters, List<Rule> rules) {
		super(name, parameters);
		this.rules = rules;
		for (Rule r : new ArrayList<Rule>(rules)) {
			if (r.atPredicate != null) {
				switch (r.atPredicate.kind) {
				case INIT:
					this.initRules.add(r);
					this.rules.remove(r);
					break;
				case END:
					this.endRules.add(r);
					this.rules.remove(r);
					break;
				case READY:
					this.readyRules.add(r);
					this.rules.remove(r);
					break;
				case ERROR:
					this.errorRules.add(r);
					this.rules.remove(r);
					break;
				default:
					this.atRules.add(r);
					this.rules.remove(r);
				}
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

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}

}
