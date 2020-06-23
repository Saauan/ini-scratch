package ini.eval.function;

import java.io.PrintWriter;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniContext;
import ini.IniLanguage;

@NodeInfo(shortName = "print")
@GenerateNodeFactory()
public abstract class PrintFunction extends BuiltInExecutable {
	
	public static String defaultName = "print";
	
	public PrintFunction() {
	}
	
    @Specialization
    public Number print(Number value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrint(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(PrintWriter out, Number value) {
    	out.print(value);
    }

    @Specialization
    public boolean print(boolean value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrint(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(PrintWriter out, boolean value) {
    	out.print(value);
    }
    
    @Specialization
    public String print(String value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrint(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(PrintWriter out, String value) {
    	out.print(value);
    	out.flush();
    }
    
    @Specialization
    public char print(char value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrint(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(PrintWriter out, char value) {
    	out.print(value);
    }

    @Specialization
    public Object print(Object value, @CachedContext(IniLanguage.class) IniContext context) {
        doPrint(context.getOut(), value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(PrintWriter out, Object value) {
    	out.print(value);
    }
}
