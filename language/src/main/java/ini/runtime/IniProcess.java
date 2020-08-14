package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.IniRootNode;
import ini.ast.ProcessExecutor;

public class IniProcess extends IniExecutable {
	
	/* The current number of lambda functions created */
	private static int nbLambdas = 0;

	public IniProcess(RootCallTarget callTarget, String name) {
		super(callTarget, name);
	}
	
	public static IniProcess createStatic(IniLanguage lang, String name, FrameSlot[] parametersSlots, ini.ast.Process process,
			FrameDescriptor frameDescriptor) {
		AstElement[] bodyNodes = {new ProcessExecutor(process)};
		return new IniProcess(Truffle.getRuntime()
				.createCallTarget(IniRootNode.create(lang, name, parametersSlots, bodyNodes, frameDescriptor))
													, (name == null) ? getLambdaId() : name);
	}
	
	public static String getLambdaId() {
		return String.format("lambda_process-%s", nbLambdas);
	}

}
