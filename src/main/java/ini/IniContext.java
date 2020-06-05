package ini;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.ast.AstElement;
import ini.eval.function.BuiltInExecutable;
import ini.eval.function.PrintFunctionFactory;
import ini.eval.function.PrintlnFunctionFactory;
/**
 * The run-time state of INI during execution. The context is created by the {@link IniLanguage}.
 * <p>
 * It would be an error to have two different context instances during the execution of one script.
 * However, if two separate scripts run in one Java VM at the same time, they have a different
 * context. Therefore, the context is not a singleton.
 */
public class IniContext {
	    private final FrameDescriptor globalFrameDescriptor;
	    private final MaterializedFrame globalFrame;
	    private final IniLanguage lang;
//	    private final InputStream in;
//	    private final PrintStream out;

	    public IniContext() {
	        this(null);
	    }

	    public IniContext(IniLanguage lang) {
	        this(lang, System.in, System.out);
	    }
	    
	    public IniContext(IniLanguage lang, InputStream in, PrintStream out) {
	    	this.globalFrameDescriptor = new FrameDescriptor();
//	        this.in = in;
//	        this.out = out;
	        this.globalFrame = this.initGlobalFrame(lang, in, out);
	        this.lang = lang;
	    }

	    private MaterializedFrame initGlobalFrame(IniLanguage lang, InputStream in, PrintStream out) {
	        VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(null,
	                this.globalFrameDescriptor);
	        addSystemVariable(frame, in, out);
	        addGlobalFunctions(lang, frame);
	        return frame.materialize();
	    }

	    private static void addGlobalFunctions(IniLanguage lang, VirtualFrame virtualFrame) {
	        FrameDescriptor frameDescriptor = virtualFrame.getFrameDescriptor();
	        virtualFrame.setObject(frameDescriptor.addFrameSlot(AstElement.getFunctionIdentifier("print", 1)),
	                BuiltInExecutable.createBuiltinFunction(lang, "print", PrintFunctionFactory.getInstance(),
	                        virtualFrame));
	        virtualFrame.setObject(frameDescriptor.addFrameSlot(AstElement.getFunctionIdentifier("println", 1)),
	                BuiltInExecutable.createBuiltinFunction(lang, "println",PrintlnFunctionFactory.getInstance(),
	                        virtualFrame));
	    }
	    
	    /**
	     * The system variable is an IniEnv object
	     */
	    private static void addSystemVariable(VirtualFrame virtualFrame, InputStream in, PrintStream out) {
	    	IniEnv env = new IniEnv(in, out);
	    	virtualFrame.setObject(getSystemVariableSlot(virtualFrame.getFrameDescriptor()), env);
	    }
	    
	    private static FrameSlot getSystemVariableSlot(FrameDescriptor frameDescriptor) {
	    	return frameDescriptor.findOrAddFrameSlot(getSystemVariableIdentifier());
	    }
	    
	    private static String getSystemVariableIdentifier() {
	    	return "ini env";
	    }
	    
	    public static IniEnv getSystemVariable(VirtualFrame virtualFrame) throws FrameSlotTypeException {
			Object env = virtualFrame.getObject(getSystemVariableSlot(virtualFrame.getFrameDescriptor()));
			if (!(env instanceof IniEnv)) {
				throw new FrameSlotTypeException();
			}
			return (IniEnv) env;
	    }

	    /**
	     * @return A {@link MaterializedFrame} on the heap that contains all global
	     * values.
	     */
	    public MaterializedFrame getGlobalFrame() {
	        return this.globalFrame;
	    }
}
