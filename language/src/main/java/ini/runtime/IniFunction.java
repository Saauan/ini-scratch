package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.IniRootNode;

public class IniFunction extends IniExecutable {
	
	/* The current number of lambda functions created */
	private static int nbLambdas = 0;

	public IniFunction(RootCallTarget callTarget, String name) {
		super(callTarget, name);
	}
	
	/**
	 * Returns an IniFunction made with a callTarget (targetting a IniRootNode), and a name
	 * 
	 * @param name if the name is null, it is a lambda function
	 */
	public static IniFunction createStatic(IniLanguage lang, String name, FrameSlot[] parametersSlots, AstElement[] bodyNodes,
			FrameDescriptor frameDescriptor) {
		String correctName = (name == null) ? getLambdaId() : name;
		return new IniFunction(Truffle.getRuntime()
				.createCallTarget(IniRootNode.create(lang,correctName, parametersSlots, bodyNodes, frameDescriptor)),
													correctName);
	}
	
	public static String getLambdaId() {
		return String.format("lambda_function-%s", nbLambdas ++);
	}

}
