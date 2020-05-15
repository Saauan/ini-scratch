package ini;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

//import ini.analysis.spin.Ini2Pml;
import ini.ast.AstElement;
import ini.ast.AstNode;
import ini.ast.Executable;
import ini.ast.Invocation;
import ini.ast.Scanner;
import ini.ast.Token;
//import ini.broker.CoreBrokerClient;
//import ini.broker.DeployRequest;
//import ini.eval.Context;
//import ini.eval.IniDebug;
//import ini.eval.IniEval;
//import ini.eval.data.FutureData;
//import ini.eval.data.RawData;
//import ini.parser.IniParser;
//import ini.type.AstAttrib;
import com.oracle.truffle.api.TruffleLanguage;

/**
 * The entry point for the INI parser/evaluator.
 * 
 * @author Renaud Pawlak
 */
@TruffleLanguage.Registration(name = "INI", id="INI")
public class IniLanguage extends TruffleLanguage<Object>{
	
	private static void runIni(String filename) throws IOException {
	}

	@Override
	protected Object createContext(Env env) {
		return null;
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

}
