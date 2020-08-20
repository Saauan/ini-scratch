package ini;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ini.ast.Rule;
import ini.ast.at.At;

public class VariableWatcher {
	
	private final Map<String, Rule> watchedVariables;
	private final Map<At, List<String>> variableChangers;
	public final List<At> lastExecutedAts;
	
	public VariableWatcher() {
		this.watchedVariables = new HashMap<String, Rule>();
		this.variableChangers = new HashMap<At, List<String>>();
		this.lastExecutedAts = new ArrayList<At>();
	}
	
	public void watchVariable(String variableName, Rule associatedRule) {
		this.watchedVariables.put(variableName, associatedRule);
	}
	
	public boolean isVariableWatched(String variableName) {
		return this.watchedVariables.containsKey(variableName);
	}
	
	public Rule getAssociatedRule(String variableName) {
		return this.watchedVariables.get(variableName);
	}
	
	public void setAsVariableChanger(At at, String variableName) {
		if(variableChangers.containsKey(at)) {
			variableChangers.get(at).add(variableName);
		}
		else {
			variableChangers.put(at, new ArrayList<String>(Arrays.asList(variableName)));
		}
	}
	
	public List<String> getVariableChangers(At at) {
		return this.variableChangers.get(at);
	}
	
	public Set<Rule> getRulesThatShouldBeChecked(At at){
		Set<Rule> rules = new HashSet<Rule>();
		for(String name : this.variableChangers.get(at)) {
			assert watchedVariables.containsKey(name) : "A modified variable was not associated to a rule :" + name;
			rules.add(this.watchedVariables.get(name));
		}
		return rules;
	}
	
	public void resetVariableChangers() {
		this.variableChangers.clear();
	}

}
