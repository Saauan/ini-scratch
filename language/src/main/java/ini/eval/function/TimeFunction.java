package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniEnv;

@NodeInfo(shortName = "time")
@GenerateNodeFactory
public abstract class TimeFunction extends BuiltInExecutable {
	
	public static String defaultName = "time";
	
	public TimeFunction() {
	}

    @Specialization
    public long now() {
        return System.currentTimeMillis();
    }

}
