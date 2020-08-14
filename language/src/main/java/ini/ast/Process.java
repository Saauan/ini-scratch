package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;

import ini.IniLanguage;
import ini.runtime.IniProcess;

/* This is the class that will create the process. That is to say, put it in a registry */
@GenerateWrapper
public class Process extends Executable{

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
	
	public Process() {
		super(null, null);
	}

	@Override 
	public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new ProcessWrapper(this, probeNode);
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
		/* Each time a new function is created, a new frame descriptor is created */
		FrameDescriptor frameDescriptor = new FrameDescriptor();
		IniProcess process = IniProcess.createStatic(lookupContextReference(IniLanguage.class).get().getLang(), name,
				convertListOfParametersToArrayOfFrameSlot(parameters, frameDescriptor), this, frameDescriptor);
		
		/* Register the process if it is not a lambda process */
		if(this.name != null) {
			lookupContextReference(IniLanguage.class).get().getFunctionRegistry().register(getExecutableIdentifier(this.name, this.parameters.size()), process);
		}
		
		return process;

	}
}
