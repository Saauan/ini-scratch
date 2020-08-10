package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import ini.IniLanguage;
import ini.ast.AstElement;

public abstract class IniExecutable {
	
    public final RootCallTarget callTarget;
    public final String name;

    public IniExecutable(RootCallTarget callTarget, String name) {
        this.callTarget = callTarget;
        this.name = name;
    }
}
