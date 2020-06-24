package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.IniRootNode;
import ini.ast.Sequence;

public class IniFunction {
	
    public final RootCallTarget callTarget;
    public final String name;

    public IniFunction(RootCallTarget callTarget, String name) {
        this.callTarget = callTarget;
        this.name = name;
    }

    public static IniFunction create(IniLanguage lang, String name, FrameSlot[] parametersSlots,
    		AstElement[] bodyNodes, FrameDescriptor frameDescriptor) {
        return new IniFunction(
                Truffle.getRuntime().createCallTarget(
                        IniRootNode.create(lang, name, parametersSlots, bodyNodes, frameDescriptor)), name);
    }
}
