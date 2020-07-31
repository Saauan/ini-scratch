package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.IniRootNode;

public class IniFunction extends IniExecutable {

	public IniFunction(RootCallTarget callTarget, String name) {
		super(callTarget, name);
	}

	@Override
	public IniFunction create(IniLanguage lang, String name, FrameSlot[] parametersSlots, AstElement[] bodyNodes,
			FrameDescriptor frameDescriptor) {
		return new IniFunction(Truffle.getRuntime()
				.createCallTarget(IniRootNode.create(lang, name, parametersSlots, bodyNodes, frameDescriptor)), name);
	}
	
	public static IniFunction createStatic(IniLanguage lang, String name, FrameSlot[] parametersSlots, AstElement[] bodyNodes,
			FrameDescriptor frameDescriptor) {
		return new IniFunction(null, null).create(lang, name, parametersSlots, bodyNodes, frameDescriptor);
	}

}
