package ini;

import java.io.InputStream;
import java.io.PrintStream;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
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
import ini.eval.function.TimeFunctionFactory;

/**
 * The run-time state of INI during execution. The context is created by the
 * {@link IniLanguage}.
 * <p>
 * It would be an error to have two different context instances during the
 * execution of one script. However, if two separate scripts run in one Java VM
 * at the same time, they have a different context. Therefore, the context is
 * not a singleton.
 */
public class IniContext {

	private final FrameDescriptor globalFrameDescriptor;

	private final MaterializedFrame globalFrame;

	private final IniLanguage lang;

	private final IniFunctionRegistry functionRegistry;
	private final InputStream in;
	private final PrintStream out;

	@Deprecated
	public IniContext() {
		this(null);
	}

	public IniContext(IniLanguage lang) {
		this(lang, System.in, System.out);
	}

	public IniContext(IniLanguage lang, InputStream in, PrintStream out) {
		this.globalFrameDescriptor = new FrameDescriptor();
		this.in = in;
		this.out = out;
		this.lang = lang;
		this.functionRegistry = new IniFunctionRegistry(lang);
		this.globalFrame = this.initGlobalFrame(lang, in, out);
		
		
	}
	

	private MaterializedFrame initGlobalFrame(IniLanguage lang, InputStream in, PrintStream out) {
		VirtualFrame frame = Truffle.getRuntime().createVirtualFrame(null, this.globalFrameDescriptor);
		addSystemVariable(frame, in, out);
		installBuiltins();
		return frame.materialize();
	}

	/**
	 * The system variable is an IniEnv object
	 */
	private static void addSystemVariable(VirtualFrame virtualFrame, InputStream in, PrintStream out) {
		IniEnv env = new IniEnv(in, out);
		virtualFrame.setObject(getSystemVariableSlot(virtualFrame.getFrameDescriptor()), env);
	}

	public static IniEnv getSystemVariable(VirtualFrame virtualFrame) throws FrameSlotTypeException {
		Object env = virtualFrame.getObject(getSystemVariableSlot(virtualFrame.getFrameDescriptor()));
		if (!(env instanceof IniEnv)) {
			throw new FrameSlotTypeException();
		}
		return (IniEnv) env;
	}

	private static String getSystemVariableIdentifier() {
		return "ini env";
	}

	private static FrameSlot getSystemVariableSlot(FrameDescriptor frameDescriptor) {
		return frameDescriptor.findOrAddFrameSlot(getSystemVariableIdentifier());
	}

	public IniFunctionRegistry getFunctionRegistry() {
		return functionRegistry;
	}
	
	public InputStream getIn() {
		return in;
	}
	
	public PrintStream getOut() {
		return out;
	}

	public IniLanguage getLang() {
		return lang;
	}

	/**
	 * @return A {@link MaterializedFrame} on the heap that contains all global
	 *         values.
	 */
	public MaterializedFrame getGlobalFrame() {
		return this.globalFrame;
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

	private void installBuiltins() {
		installBuiltin(PrintFunctionFactory.getInstance(), 1);
		installBuiltin(PrintlnFunctionFactory.getInstance(), 1);
		installBuiltin(TimeFunctionFactory.getInstance(), 0);
	}

	private void installBuiltin(NodeFactory<? extends BuiltInExecutable> factory, int nbParameters) {
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
			argumentNodes[i] = new ReadArgumentFromContextNode(null, null, i);
		}
		/* Instantiate the builtin node. This node performs the actual functionality. */
		/*
		 * It is an array of one element. IniRootNode accepts only arrays as body nodes
		 */
		BuiltInExecutable[] builtinBodyNodeArray = { factory.createNode((Object) argumentNodes) };
		/*
		 * The name of the builtin function is specified via an annotation on the node
		 * class.
		 */
		String name = lookupNodeInfo(builtinBodyNodeArray[0].getClass()).shortName();
		String functionId = AstElement.getFunctionIdentifier(name, nbParameters);

		/*
		 * Wrap the builtin in a RootNode. Truffle requires all AST to start with a
		 * RootNode.
		 */
		IniRootNode rootNode = new IniRootNode(lang, name, builtinBodyNodeArray, new FrameDescriptor());

		/* Register the builtin function in our function registry. */
		getFunctionRegistry().register(functionId, Truffle.getRuntime().createCallTarget(rootNode));
	}

}
