package ini.eval.function;

import java.io.PrintStream;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.AstElement;
import ini.parser.IniParser;

@NodeInfo(shortName = "print")
@NodeChild(value = "receiver", type=AstElement[].class)
@GenerateNodeFactory()
public abstract class PrintFunction extends BuiltInExecutable {
	
	public static String defaultName = "print";

	public PrintFunction(String[] parameterNames) {
		super(parameterNames);
	}

	private static final PrintStream out = System.out;
	
    @Specialization
    public Number print(Number value) {
        doPrint(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(Number value) {
    	out.print(value);
    }

    @Specialization
    public boolean print(boolean value) {
        doPrint(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(boolean value) {
    	out.print(value);
    }
    
    @Specialization
    public char print(char value) {
        doPrint(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(char value) {
    	out.print(value);
    }
    
    @Specialization
    public String print(String value) {
        doPrint(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(String value) {
    	out.print(value);
    }

    @Specialization
    public Object print(Object value) {
        doPrint(value);
        return value;
    }
    
    @TruffleBoundary
    private static void doPrint(Object value) {
    	out.print(value);
    }
}
