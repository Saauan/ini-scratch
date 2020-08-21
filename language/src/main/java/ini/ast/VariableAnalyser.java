package ini.ast;

import ini.VariableWatcher;
import ini.ast.at.At;

public class VariableAnalyser extends Scanner {

	private boolean lookingForVariable = false;
	private Rule ruleToAssociate;
	
	private boolean lookingForAssignment = false;
	private At atToAssociate;
	
	private VariableWatcher watcher;
	
	public VariableAnalyser(VariableWatcher watcher) {
		this.watcher = watcher;
	}
	
	@Override
	public void visitProcess(Process process) {
		Rule[] rulesToWatch = process.rules;
		for(Rule rule : rulesToWatch) {
			AstExpression guard = rule.guard;
			lookingForVariable = true;
			ruleToAssociate = rule;
			scan(guard);
			lookingForVariable = false;
		}
		super.visitProcess(process);
	}
	
	@Override
	public void visitVariable(Variable variable) {
		if(lookingForVariable) {
//			System.err.println(String.format("Watching variable %s, for rule %s", variable.name, ruleToAssociate));
			watcher.watchVariable(variable.name, ruleToAssociate);
		}
		super.visitVariable(variable);
	}
	
	@Override
	public void visitAssignment(Assignment assignment) {
		if(lookingForAssignment) {
			if(assignment.assignee instanceof Variable) {
				Variable modifiedVar = (Variable) assignment.assignee;
				System.err.println(String.format("At %s modifies variable %s", atToAssociate, modifiedVar.name));
				watcher.setAsVariableChanger(atToAssociate, modifiedVar.name);
			}
		}
		super.visitAssignment(assignment);
	}
	
	@Override
	public void visitAt(At at) {
		// My implementation is not correct, One at can be at the same time a child of an AtPredicate
		// and a child of a Process. Must correct this
//		Rule ruleToWatch = at.getRule();
//		AstElement[] statements = ruleToWatch.statements;
//		lookingForAssignment = true;
//		atToAssociate = at;
//		scan(statements);
//		lookingForAssignment = false;
		super.visitAt(at);
	}
}
