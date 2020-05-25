package ini;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
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

	    public IniContext() {
	        this(null);
	    }

	    public IniContext(IniLanguage lang) {
	        this.globalFrameDescriptor = new FrameDescriptor();
	        this.globalFrame = this.initGlobalFrame(lang);
	        this.lang = lang;
	    }

	    private MaterializedFrame initGlobalFrame(IniLanguage lang) {
	        VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(null,
	                this.globalFrameDescriptor);
	        addGlobalFunctions(lang, frame);
	        return frame.materialize();
	    }

	    private static void addGlobalFunctions(IniLanguage lang, VirtualFrame virtualFrame) {
	        FrameDescriptor frameDescriptor = virtualFrame.getFrameDescriptor();
	    }

	    /**
	     * @return A {@link MaterializedFrame} on the heap that contains all global
	     * values.
	     */
	    public MaterializedFrame getGlobalFrame() {
	        return this.globalFrame;
	    }
}
