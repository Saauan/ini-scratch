package ini.eval.function;

import java.io.PrintWriter;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniContext;
import ini.IniLanguage;

@NodeInfo(shortName = "println")
@GenerateNodeFactory()
public abstract class PrintlnFunction extends BuiltInExecutable {
	
	public static String defaultName = "println";
	
	public PrintlnFunction() {
	}
	

    @Specialization
    public Number println(Number value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrintln(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(PrintWriter out, Number value) {
    	out.println(value);
    }

    @Specialization
    public boolean println(boolean value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrintln(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(PrintWriter out, boolean value) {
    	out.println(value);
    }
    
    @Specialization
    public String println(String value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrintln(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(PrintWriter out, String value) {
    	out.println(value);
    }
    
    @Specialization
    public char println(char value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrintln(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(PrintWriter out, char value) {
    	out.println(value);
    }

    @Specialization
    public Object println(Object value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrintln(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrintln(PrintWriter out, Object value) {
    	out.println(value);
    }
}
