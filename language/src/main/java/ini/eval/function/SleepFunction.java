package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "sleep")
@GenerateNodeFactory
public abstract class SleepFunction extends BuiltInExecutable {
	
	public static String defaultName = "time";
	
	public SleepFunction() {
	}

    @Specialization
    public int sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			// BUG : Throw runtimeException ?
		}
		return millis;
	}

}
