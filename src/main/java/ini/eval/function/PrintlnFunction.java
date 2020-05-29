package ini.eval.function;

import java.io.PrintStream;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniEnv;

@NodeInfo(shortName = "println")
@GenerateNodeFactory()
public abstract class PrintlnFunction extends BuiltInExecutable {
	
	public static String defaultName = "println";

	private static PrintStream out;
	
	public PrintlnFunction(IniEnv env, String[] parameterNames) {
		super(parameterNames);
		out = env.out;
	}
	
    @Specialization
    public Number println(Number value) {
        doPrintln(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(Number value) {
    	out.println(value);
    }

    @Specialization
    public boolean println(boolean value) {
        doPrintln(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(boolean value) {
    	out.println(value);
    }
    
    @Specialization
    public char println(char value) {
        doPrintln(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(char value) {
    	out.println(value);
    }
    
    @Specialization
    public String println(String value) {
        doPrintln(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(String value) {
    	out.println(value);
    }

    @Specialization
    public Object println(Object value) {
        doPrintln(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(Object value) {
    	out.println(value);
    }
}
