package ini;

import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.source.Source;

import ini.ast.AstElement;
import ini.ast.AstNode;
import ini.ast.Executable;
import ini.ast.IniRootNode;
import ini.ast.Sequence;
import ini.parser.IniParser;
import ini.runtime.IniFunction;
import ini.IniLanguage;

public class IniMain {
	public static final Logger LOGGER = LoggerFactory.getLogger("ini");
	public static final String ID = IniLanguage.ID;


    public static void main(String[] args) throws Exception {
    	MDC.put("node", ".");

		JSAP jsap = new JSAP();

		jsap.registerParameter(
				new Switch("help").setShortFlag('h').setLongFlag("help").setHelp("Prints this usage message."));

		jsap.registerParameter(new Switch("version").setLongFlag("version").setHelp("Print the INI version and exit."));

		jsap.registerParameter(new FlaggedOption("env").setLongFlag("env").setStringParser(JSAP.STRING_PARSER)
				.setRequired(false).setList(false).setHelp(
						"Defines the environment name to be used, as defined in the 'ini_conf.json' file. Overrides the value defined in the INI_ENV system environment variable."));

		jsap.registerParameter(new Switch("shell").setShortFlag('s').setLongFlag("shell")
				.setHelp("Starts INI in shell mode so that the user can interact with INI by typing statements."));

		jsap.registerParameter(new FlaggedOption("node").setLongFlag("node").setShortFlag('n')
				.setStringParser(JSAP.STRING_PARSER).setRequired(false).setList(false).setHelp(
						"Sets the node name and starts INI in deamon mode. The name of the node is used by other INI nodes to spawn and fetch processes and functions. This option overrides the value defined in the INI_NODE system environment variable or in the 'ini_conf.json'."));

		jsap.registerParameter(new FlaggedOption("model-out").setLongFlag("model-out")
				.setStringParser(JSAP.STRING_PARSER).setRequired(false).setList(false).setHelp(
						"Generates the Promela model into the given file, so that it can be checked by the Spin model checker."));

		jsap.registerParameter(new UnflaggedOption("file").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(false)
				.setHelp("The INI file that must be parsed/executed (may contain a main function to be executed)."));

		jsap.registerParameter(new UnflaggedOption("arg").setStringParser(JSAP.STRING_PARSER).setRequired(false)
				.setList(true).setGreedy(true)
				.setHelp("The arguments to be passed to the 'main' function, if defined in the given file."));

		JSAPResult commandLineConfig = jsap.parse(args);
		
		if (commandLineConfig.getBoolean("help")
				|| (!commandLineConfig.userSpecified("node") && !commandLineConfig.userSpecified("file")
						&& !commandLineConfig.userSpecified("shell") && !commandLineConfig.userSpecified("version"))) {
			printUsage(System.out, jsap);
			System.exit(0);
		}
//		LOGGER.debug("INI version " + VERSION);
		if (commandLineConfig.getBoolean("version")) {
			System.exit(0);
		}
		if (!commandLineConfig.success()) {
			System.err.println();
			for (java.util.Iterator<?> errs = commandLineConfig.getErrorMessageIterator(); errs.hasNext();) {
				System.err.println("Error: " + errs.next());
			}

			System.err.println();
			printUsage(System.err, jsap);
			System.exit(-1);
		}
		String filename;
		if(commandLineConfig.contains("file")) {
			filename = commandLineConfig.getString("file");
		}
		else {
			filename = "";
		}
        Source source = Source.newBuilder(ID, new FileReader(new File(filename)), filename).build();
        IniContext context = new IniContext();
        
        /* Parse source */
		IniParser parser = commandLineConfig.contains("file")
				? IniParser.createParserForFile(null, null, commandLineConfig.getString("file"))
				: IniParser.createParserForCode(null, null, "process main() {}");

		try {
			parser.parse();
		} catch (Exception e) {
			if (parser.hasErrors()) {
				parser.printErrors(System.err);
				e.printStackTrace();
				return;
			} else {
				e.printStackTrace();
				return;
			}
		}
        
        /* Execute all nodes */
        AstElement[] topLevelNodes = parser.topLevels.toArray(new AstElement[0]);
        execute(topLevelNodes, context.getGlobalFrame());
    }

//    private static void runIni(String filename) throws IOException {
//        Source source = Source.newBuilder(ID, new FileReader(new File(filename)), filename).build();
//        IniContext context = new IniContext();
//        ListSyntax sexp = Reader.read(source);
//        Converter converter = new Converter(null, flags.tailCallOptimizationEnabled);
//        IniNode[] nodes = converter.convertSexp(context, sexp);
//        execute(nodes, context.getGlobalFrame());
//    }

    /**
     * Wraps all top level nodes in a function and executes the function
     * @param out
     * @param jsap
     */
    private static Object execute(AstElement[] nodes, MaterializedFrame globalFrame) {
        IniFunction function = IniFunction.create(
        		null,
        		new FrameSlot[] {},
        		convertArrayToSequence(nodes),
        		globalFrame.getFrameDescriptor());
        ((IniRootNode) function.callTarget.getRootNode()).setName("main");

        return function.callTarget.call(globalFrame);
    }
    
    private static Sequence<AstElement> convertArrayToSequence(AstElement[] nodes){
    	final int nbNodes = nodes.length;
    	Sequence<AstElement> res = new Sequence<>(nodes[0]);
    	for(int i=nbNodes-1; i>0; i--) {
    		res.insertNext(nodes[i]);
    	}
    	return res;
    }
	static void printUsage(PrintStream out, JSAP jsap) {
		out.println("Usage: ini " + jsap.getUsage());
		out.println();
		out.println(jsap.getHelp());
	}
    
}
