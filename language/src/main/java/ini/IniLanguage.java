package ini;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.martiansoftware.jsap.JSAP;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.source.Source;

import ini.ast.AstElement;
import ini.ast.AstNode;
import ini.ast.Executable;
import ini.ast.Function;
import ini.ast.Invocation;
import ini.parser.IniParser;
import ini.runtime.IniFunction;

/**
 * The entry point for the INI parser/evaluator.
 * 
 * @author Renaud Pawlak
 */
@TruffleLanguage.Registration(name = "INI", id="INI")
public class IniLanguage extends TruffleLanguage<IniContext>{
	
	public static final Logger LOGGER = LoggerFactory.getLogger("ini");

	public static final String VERSION = "pre-alpha 2-truffle";
	
	public static final String ID = "INI";
	public static final String MIME_TYPE = "application/x-ini";
	
	public static final String ROOT_FUNCTION_NAME = "root_function:main";
	

	@Override
	protected IniContext createContext(Env env) {
		return new IniContext(this, env);
	}
	
    @Override
    protected void initializeContext(IniContext context) throws Exception {
    	IniLanguage.LOGGER.debug("Initializing context");
    }
	
	@Override
	protected void finalizeContext(IniContext context) {
        // stop and join all the created Threads
		IniLanguage.LOGGER.debug("Finalizing context");
        boolean interrupted = false;
        for (int i = 0; i < context.startedThreads.size();) {
            Thread threadToJoin  = context.startedThreads.get(i);
            try {
                if (threadToJoin != Thread.currentThread()) {
                    threadToJoin.interrupt();
                    threadToJoin.join();
                }
                i++;
            } catch (InterruptedException ie) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
	
	@Override
    protected void initializeThread(IniContext context, Thread thread) {
		IniLanguage.LOGGER.debug("New thread :" + thread);
    }

    @Override
    protected void disposeThread(IniContext context, Thread thread) {
    	IniLanguage.LOGGER.debug("Disposed thread :" + thread);
    }
	
    public static IniContext getCurrentContext() {
        return getCurrentContext(IniLanguage.class);
    }
    
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception{
    	Source source = request.getSource();
		IniParser parser = IniParser.createParserForCode(null, null, source.getCharacters().toString());
		try {
			parser.parse();
		} catch (Exception e) {
			if (parser.hasErrors()) {
				parser.printErrors(System.err);
				e.printStackTrace();
				System.exit(1);
			} else {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		MaterializedFrame globalFrame = getCurrentContext().getGlobalFrame();
		/* If there is a main, we just add a call to the main function at the end of the program */
		if(isMainFunctionPresent(parser)) {
			parser.topLevels.add(new Invocation("main", null));
		}				
		AstElement[] topLevelNodes = parser.topLevels.toArray(new AstElement[0]);
		IniFunction root = wrapNodesAndCreateCallTarget(topLevelNodes, globalFrame);
        return root.callTarget;
    }

	private IniFunction wrapNodesAndCreateCallTarget(AstElement[] topLevelNodes, MaterializedFrame globalFrame) {
		IniFunction function = IniFunction.createStatic(
        		null,
        		ROOT_FUNCTION_NAME,
        		new FrameSlot[] {},
        		topLevelNodes,
        		globalFrame.getFrameDescriptor());
		return function;
	}

	/**
	 * Returns true if there is a main function definition in the program
	 */
	public boolean isMainFunctionPresent(IniParser parser) {
		for (AstNode topLevel : parser.topLevels) {
			if ((topLevel instanceof Executable) && "main".equals(((Executable) topLevel).name)) {
				return true;
			}
		}
		return false;
	}

	public static void parseConfiguration(IniParser parser) {
		try {
			parser.env.configuration = new Gson()
					.fromJson(FileUtils.readFileToString(new File("ini_config.json"), "UTF8"), ConfigurationFile.class);
		} catch (Exception e) {
			throw new RuntimeException("cannot read configuration", e);
		}
	}

	static void printUsage(PrintStream out, JSAP jsap) {
		out.println("Usage: ini " + jsap.getUsage());
		out.println();
		out.println(jsap.getHelp());
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		return false;
	}
	
	@Override
	protected boolean isThreadAccessAllowed(Thread thread,
            boolean singleThreaded) {
		return true;
	}
}
