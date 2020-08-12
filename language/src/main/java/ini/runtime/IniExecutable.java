package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;

public abstract class IniExecutable {
	
    public final RootCallTarget callTarget;
    public final String name;

    public IniExecutable(RootCallTarget callTarget, String name) {
        this.callTarget = callTarget;
        this.name = name;
    }
}
