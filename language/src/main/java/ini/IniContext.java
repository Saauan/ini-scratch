package ini;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.AstElement;
import ini.ast.AstExpression;
import ini.ast.IniRootNode;
import ini.ast.ReadArgumentFromContextNode;
import ini.eval.function.BuiltInExecutable;
import ini.eval.function.PrintFunctionFactory;
import ini.eval.function.PrintlnFunctionFactory;
import ini.eval.function.ProduceFunctionFactory;
import ini.eval.function.SizeFunctionFactory;
import ini.eval.function.SleepFunctionFactory;
import ini.eval.function.StopFunctionFactory;
import ini.eval.function.TimeFunctionFactory;
import ini.runtime.IniFunction;
import ini.runtime.ProcessRunner;

/**
 * The run-time state of INI during execution. The context is created by the
 * {@link IniLanguage}.
 * 
 * It would be an error to have two different context instances during the
 * execution of one script. However, if two separate scripts run in one Java VM
 * at the same time, they have a different context. Therefore, the context is
 * not a singleton.
 */
public class IniContext {

	/* The global frame is the rootFrame */
	private final MaterializedFrame globalFrame;
	private final FrameDescriptor globalFrameDescriptor;
	/* globalVariable are variables that are available in every frame. They must be set in the root frame
	 * (name, value) */
	private Map<String, Object> globalVariables;
	private final IniFunctionRegistry functionRegistry;
	
	public final VariableWatcher varWatcher;
	
	private final IniLanguage lang;
	
	private final InputStream in;
	private final PrintWriter out;
	private TruffleLanguage.Env env;
	
	
	private final Set<String> importedFiles;
	public List<Thread> startedThreads;
	public List<ProcessRunner> startedProcesses;

	public IniContext(IniLanguage lang, TruffleLanguage.Env env) {
		this.globalFrameDescriptor = new FrameDescriptor();
		this.in = env.in();
		this.out = new PrintWriter(env.out(), true);
		this.lang = lang;
		this.functionRegistry = new IniFunctionRegistry(lang);
		this.varWatcher = new VariableWatcher(); 
		this.globalFrame = this.initGlobalFrame(lang);
		this.globalVariables = new HashMap<String, Object>();
		this.importedFiles = new HashSet<String>();
		this.env = env;
		this.startedThreads = new ArrayList<Thread>();
		this.startedProcesses = new ArrayList<ProcessRunner>();
	}

	private MaterializedFrame initGlobalFrame(IniLanguage lang) {
		VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(null, this.globalFrameDescriptor);
		installBuiltins(frame);
		return frame.materialize();
	}

	public TruffleLanguage.Env getEnv() {
		return env;
	}

	public IniFunctionRegistry getFunctionRegistry() {
		return functionRegistry;
	}

	public InputStream getIn() {
		return in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public IniLanguage getLang() {
		return lang;
	}

	public Set<String> getImportedFiles() {
		return importedFiles;
	}

	public void addImportedFile(String filePath) {
		if (isFileImported(filePath)) {
			throw new RuntimeException("Cannot add twice the same imported file");
		} else {
			importedFiles.add(filePath);
		}
	}

	public boolean isFileImported(String filePath) {
		return importedFiles.contains(filePath);
	}

	/**
	 * @return A {@link MaterializedFrame} on the heap that contains all global
	 *         values.
	 */
	public MaterializedFrame getGlobalFrame() {
		return this.globalFrame;
	}
	
	/**
	 * When a variable is added, each time a new IniRootNode is created, the new frame will contain the variable
	 * @param name
	 * @param value
	 */
	public void addGlobalVariable(String name, Object value) {
		this.globalVariables.put(name, value);
	}
	
	public Map<String, Object> getGlobalVariables(){
		return this.globalVariables;
	}

	public static NodeInfo lookupNodeInfo(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		NodeInfo info = clazz.getAnnotation(NodeInfo.class);
		if (info != null) {
			return info;
		} else {
			return lookupNodeInfo(clazz.getSuperclass());
		}
	}

	private void installBuiltins(VirtualFrame frame) {
		MaterializedFrame materializedFrame = frame.materialize();
		installBuiltin(materializedFrame, PrintFunctionFactory.getInstance(), 1);
		installBuiltin(materializedFrame, PrintlnFunctionFactory.getInstance(), 1);
		installBuiltin(materializedFrame, ProduceFunctionFactory.getInstance(), 2);
		installBuiltin(materializedFrame, SleepFunctionFactory.getInstance(), 1);
		installBuiltin(materializedFrame, SizeFunctionFactory.getInstance(), 1);
		installBuiltin(materializedFrame, StopFunctionFactory.getInstance(), 1);
		installBuiltin(materializedFrame, TimeFunctionFactory.getInstance(), 0);
	}

	private void installBuiltin(MaterializedFrame frame, NodeFactory<? extends BuiltInExecutable> factory,
			int nbParameters) {

		/*
		 * The builtin node factory is a class that is automatically generated by the
		 * Truffle DSL. The signature returned by the factory reflects the signature of
		 * the @Specialization
		 *
		 * methods in the builtin classes.
		 */
		final int argumentCount = factory.getExecutionSignature().size();
		AstExpression[] argumentNodes = new AstExpression[argumentCount];
		for (int i = 0; i < argumentCount; i++) {
			argumentNodes[i] = new ReadArgumentFromContextNode(i);
		}
		/* Instantiate the builtin node. This node performs the actual functionality. */
		BuiltInExecutable[] builtinBodyNodeArray = { factory.createNode((Object) argumentNodes) };
		/*
		 * The name of the builtin function is specified via an annotation on the node
		 * class.
		 */
		String name = lookupNodeInfo(builtinBodyNodeArray[0].getClass()).shortName();
		String functionId = AstElement.getExecutableIdentifier(name, nbParameters);

		/*
		 * Wrap the builtin in a RootNode. Truffle requires all AST to start with a
		 * RootNode.
		 */
		IniRootNode rootNode = new IniRootNode(lang, name, builtinBodyNodeArray, new FrameDescriptor());
		IniFunction function = new IniFunction(Truffle.getRuntime().createCallTarget(rootNode), name);

		/* Register the builtin function in our function registry. */
		getFunctionRegistry().register(functionId, function);
	}

}
