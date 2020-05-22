package ini;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.object.Shape;
/**
 * The run-time state of INI during execution. The context is created by the {@link IniLanguage}.
 * <p>
 * It would be an error to have two different context instances during the execution of one script.
 * However, if two separate scripts run in one Java VM at the same time, they have a different
 * context. Therefore, the context is not a singleton.
 */
public class IniContext {
    private final Env env;
    private final BufferedReader input;
    private final PrintWriter output;
//    private final SLFunctionRegistry functionRegistry;
    private final Shape emptyShape;
    private final IniLanguage language;
    private final AllocationReporter allocationReporter;
    private final Iterable<Scope> topScopes; // Cache the top scopes

    public IniContext(IniLanguage language, TruffleLanguage.Env env
//    		,List<NodeFactory<? extends SLBuiltinNode>> externalBuiltins
    		) {
        this.emptyShape = null;
		this.topScopes = null;
		this.env = env;
        this.input = new BufferedReader(new InputStreamReader(env.in()));
        this.output = new PrintWriter(env.out(), true);
        this.language = language;
        this.allocationReporter = env.lookup(AllocationReporter.class);
//        this.functionRegistry = new SLFunctionRegistry(language);
//        this.topScopes = Collections.singleton(Scope.newBuilder("global", functionRegistry.getFunctionsObject()).build());
//        installBuiltins();
//        for (NodeFactory<? extends SLBuiltinNode> builtin : externalBuiltins) {
//            installBuiltin(builtin);
//        }
//        this.emptyShape = LAYOUT.createShape(SLObjectType.SINGLETON);
    }
    
    /**
     * Return the current Truffle environment.
     */
    public Env getEnv() {
        return env;
    }
    
    /**
     * Returns the default input, i.e., the source for the {@link SLReadlnBuiltin}. To allow unit
     * testing, we do not use {@link System#in} directly.
     */
    public BufferedReader getInput() {
        return input;
    }

    /**
     * The default default, i.e., the output for the {@link SLPrintlnBuiltin}. To allow unit
     * testing, we do not use {@link System#out} directly.
     */
    public PrintWriter getOutput() {
        return output;
    }
    
//    /**
//     * Returns the registry of all functions that are currently defined.
//     */
//    public SLFunctionRegistry getFunctionRegistry() {
//        return functionRegistry;
//    }

    
    public Iterable<Scope> getTopScopes() {
        return topScopes;
    }
    
    /**
     * Adds all builtin functions to the {@link SLFunctionRegistry}. This method lists all
     * {@link SLBuiltinNode builtin implementation classes}.
     */
    private void installBuiltins() {}
}
