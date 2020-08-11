package ini.ast;

import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import ini.IniLanguage;

/**
 * The Root Node is the root node of an execution tree. It executes a list of
 * statements.
 * 
 * @author Tristan
 *
 */
public class IniRootNode extends RootNode {

	/** The name of the function, for printing purposes only. */
	private final String name;

	@Children
	private final AstElement[] bodyNodes;

	public IniRootNode(IniLanguage language, String name, AstElement[] bodyNodes, FrameDescriptor frameDescriptor) {
		super(language, frameDescriptor);
		this.name = name;
		this.bodyNodes = bodyNodes;
	}

	@Override
	@ExplodeLoop
	public Object execute(VirtualFrame frame) {
		// If it is not the root function (main)
		if(name != IniLanguage.ROOT_FUNCTION_NAME) {
			setUpGlobalVariables(frame);
		}
		AstElement[] s = this.bodyNodes;
		Object res = 0;
		final int nbNodes = s.length;
		int i;
		try {
			for (i = 0; i < nbNodes - 1; i++) {
				s[i].executeVoid(frame);
			}
			/*
			 * For the last node to execute. If it returns a result, simply return its
			 * result, otherwise return 0
			 */
			if (s[i] instanceof AstExpression) {
				res = ((AstExpression) s[i]).executeGeneric(frame);
			} else {
				s[i].executeVoid(frame);
				res = 0;
			}
		} catch (ReturnException e) {
			res = e.getResult();
		}
		if(name == IniLanguage.ROOT_FUNCTION_NAME) {
			try {
				waitForProcessToEnd(frame);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return res;
	}
	
	/*
	 * Adds all the global variable to the current frame 
	 */
	private void setUpGlobalVariables(VirtualFrame frame) {
		Map<String, Object> globalVariables = lookupContextReference(IniLanguage.class).get().getGlobalVariables();
		for(Map.Entry<String, Object> globalVariablesEntry : globalVariables.entrySet()) {
			String name = globalVariablesEntry.getKey();
			Object value = globalVariablesEntry.getValue();
			frame.setObject(frame.getFrameDescriptor().findOrAddFrameSlot(name), value);
		}
		
	}
	
	private void waitForProcessToEnd(VirtualFrame frame) throws InterruptedException {
		List<ProcessRunner> startedProcess = lookupContextReference(IniLanguage.class).get().startedProcesses;
		for (ProcessRunner process : startedProcess) {
			process.waitForProcessToEnd();
		}
	}

	@ExplodeLoop
	public static IniRootNode create(IniLanguage lang, String name, FrameSlot[] parametersSlots, AstElement[] bodyNodes,
			FrameDescriptor frameDescriptor) {
		final int nbParams = parametersSlots.length;
		CompilerAsserts.partialEvaluationConstant(nbParams);
		AstElement[] allNodes = new AstElement[nbParams + bodyNodes.length];
		// If there are parameters, create Assignments to read the argument passed in
		// the function and write them to local variables
		if (nbParams > 0) {
			for (int arg_index = 0; arg_index < nbParams; arg_index++) {
				allNodes[arg_index] = createAssignment(parametersSlots, arg_index);
			}
		}
		System.arraycopy(bodyNodes, 0, allNodes, nbParams, bodyNodes.length);
		return new IniRootNode(lang, name, allNodes, frameDescriptor);
	}

	/**
	 * Creates an assignment to read the argument given at function invocation and
	 * writes it in the frame
	 */
	private static Assignment createAssignment(FrameSlot[] argumentNames, int arg_index) {
		return AssignmentNodeGen.create(
				VariableNodeGen.create(argumentNames[arg_index].getIdentifier().toString(), argumentNames[arg_index]),
				argumentNames[arg_index], new ReadArgumentFromContextNode(arg_index));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "root " + name;
	}

}
