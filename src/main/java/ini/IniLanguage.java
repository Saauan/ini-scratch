package ini;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;

import ini.ast.AstElement;
import ini.ast.AstNode;
import ini.ast.Executable;
import ini.parser.IniParser;

/**
 * The entry point for the INI parser/evaluator.
 * 
 * @author Renaud Pawlak
 */
@TruffleLanguage.Registration(name = "INI", id="INI")
public class IniLanguage extends TruffleLanguage<Object>{
	
	public static final Logger LOGGER = LoggerFactory.getLogger("ini");

	public static final String VERSION = "pre-alpha 2";
	
	public static final String ID = "INI";
	public static final String MIME_TYPE = "application/x-ini";
	
//	private static void runIni(String filename) throws IOException {
//	}

	@Override
	protected IniContext createContext(Env env) {
		return new IniContext();
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
