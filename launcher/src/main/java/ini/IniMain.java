package ini;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class IniMain {
//	public static final Logger LOGGER = LoggerFactory.getLogger("ini");
	public static final String ID = "INI";


    public static void main(String[] args) throws IOException {
//    	MDC.put("node", ".");
    	Source source;
    	Map<String, String> options = new HashMap<>();
    	String file = null;
    	for (String arg : args) {
    		if(parseOption(options, arg)) {
    			continue;
    		}
    		else {
    			if (file == null) {
    				file = arg;
    			}
    		}
    	}
		
//		LOGGER.debug("INI version " + VERSION);
		if (options.containsKey("version")) {
			System.exit(0);
		}
		if (file == null) {
			System.err.println("A file must be specified !");
			source = Source.newBuilder(ID, new InputStreamReader(System.in), "<stdin>").build();
			System.exit(1);
		}
		else {
			source = Source.newBuilder(ID, new File(file)).build();
		}
		System.exit(executeSource(source, System.in, System.out, options));
//        IniContext context = new IniContext();
//        
//        /* Parse source */
//		IniParser parser = commandLineConfig.contains("file")
//				? IniParser.createParserForFile(null, null, commandLineConfig.getString("file"))
//				: IniParser.createParserForCode(null, null, "process main() {}");
//
//		try {
//			parser.parse();
//		} catch (Exception e) {
//			if (parser.hasErrors()) {
//				parser.printErrors(System.err);
//				e.printStackTrace();
//				return;
//			} else {
//				e.printStackTrace();
//				return;
//			}
//		}
//        
//        /* Execute all nodes */
//        AstElement[] topLevelNodes = parser.topLevels.toArray(new AstElement[0]);
//        execute(topLevelNodes, context.getGlobalFrame());
    }
    
    private static int executeSource(org.graalvm.polyglot.Source source, InputStream in, PrintStream out, Map<String, String> options) {
    	Context context;
    	PrintStream err = System.err;
    	try {
    		context = Context.newBuilder(ID).in(in).out(out).options(options).build();
    	} catch (IllegalArgumentException e) {
    		err.println(e.getMessage());
    		return 1;
    	}
    	out.println("== running on " + context.getEngine());
    	
    	try { 
    		Value result = context.eval(source);
//    		if (context.getBindings(ID).getMember("main") == null) {
//    			err.println("No function main() in INI source file");
//    			return 1;
//    		}
//    		if(!result.isNull()) {
//    			out.println("result : " + result.toString());
//    		}
    		return 0;
        } catch (PolyglotException ex) {
            if (ex.isInternalError()) {
                // for internal errors we print the full stack trace
                ex.printStackTrace();
            } else {
                err.println(ex.getMessage());
            }
            return 1;
        } finally {
            context.close();
        }
    }
    
    private static boolean parseOption(Map<String, String> options, String arg) {
        if (arg.length() <= 2 || !arg.startsWith("--")) {
            return false;
        }
        int eqIdx = arg.indexOf('=');
        String key;
        String value;
        if (eqIdx < 0) {
            key = arg.substring(2);
            value = null;
        } else {
            key = arg.substring(2, eqIdx);
            value = arg.substring(eqIdx + 1);
        }

        if (value == null) {
            value = "true";
        }
        int index = key.indexOf('.');
        String group = key;
        if (index >= 0) {
            group = group.substring(0, index);
        }
        options.put(key, value);
        return true;
    }

//    /**
//     * Wraps all top level nodes in a function and executes the function
//     * @param out
//     * @param jsap
//     */
//    public static Object execute(AstElement[] nodes, MaterializedFrame globalFrame) {
//        IniFunction function = IniFunction.create(
//        		null,
//        		"main",
//        		new FrameSlot[] {},
//        		nodes,
//        		new FrameDescriptor());
//        function.setLexicalScope(globalFrame);
//        return function.callTarget.call(globalFrame);
//    }
//    
//    private static Sequence<AstElement> convertArrayToSequence(AstElement[] nodes){
//    	final int nbNodes = nodes.length;
//    	Sequence<AstElement> res = new Sequence<>(nodes[0]);
//    	for(int i=nbNodes-1; i>0; i--) {
//    		res.insertNext(nodes[i]);
//    	}
//    	return res;
//    }
//
//	static void printUsage(PrintStream out, JSAP jsap) {
//		out.println("Usage: ini " + jsap.getUsage());
//		out.println();
//		out.println(jsap.getHelp());
//	}
    
}
