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
import ini.ast.IniRootNode;
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

	public static final String VERSION = "pre-alpha 2";
	
	public static final String ID = "INI";
	public static final String MIME_TYPE = "application/x-ini";
	
	

	@Override
	protected IniContext createContext(Env env) {
		return new IniContext(this);
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
		AstElement[] topLevelNodes = parser.topLevels.toArray(new AstElement[0]);
		MaterializedFrame globalFrame = getCurrentContext().getGlobalFrame();
		IniFunction function = wrapNodesAndCreateCallTarget(topLevelNodes, globalFrame);
        return function.callTarget;
    }

    
	private IniFunction wrapNodesAndCreateCallTarget(AstElement[] topLevelNodes, MaterializedFrame globalFrame) {
		IniFunction function = IniFunction.create(
        		null,
        		"main",
        		new FrameSlot[] {},
        		topLevelNodes,
        		globalFrame.getFrameDescriptor());
        function.setLexicalScope(globalFrame);
//        ((IniRootNode) function.callTarget.getRootNode()).setName("main"); // TODO : Make a real main
		return function;
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		// TODO Auto-generated method stub
		return false;
	}
	public static Executable getMainExecutable(IniParser parser) {
		Executable main = null;
		for (AstNode topLevel : parser.topLevels) {
			if ((topLevel instanceof Executable) && "main".equals(((Executable) topLevel).name)) {
				main = (Executable) topLevel;
			}
		}
		return main;
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
}
