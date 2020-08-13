package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;

import ini.IniContext;
import ini.IniLanguage;
import ini.runtime.ProcessRunner;

/* That is the class that will be called to execute the process 
 * Wraps a Process Node so as to access its rules */
@GenerateWrapper
public class ProcessExecutor extends AstExpression{
	
	@Child
	private Process wrappedProcess;

	public ProcessExecutor(Process wrappedProcess) {
		this.wrappedProcess = wrappedProcess;
	}
	
	/*
	 * This constructor is necessary for @GenerateWrapper to work */
	public ProcessExecutor() {}


	@Override
	public Object executeGeneric(VirtualFrame frame) {
		ProcessReturnValue returnValue = new ProcessReturnValue();
		ProcessRunner runner = new ProcessRunner(returnValue, frame.materialize(), wrappedProcess);
		
		Env env = lookupContextReference(IniLanguage.class).get().getEnv();
		IniLanguage.LOGGER.debug("Creating a thread in ProcessExecutor");
		Thread processThread = env.createThread(runner, env.getContext());
		IniContext context = lookupContextReference(IniLanguage.class).get(); 
		context.startedThreads.add(processThread);
		context.startedProcesses.add(runner);
		processThread.start();
		return returnValue;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("executor for process :");
		wrappedProcess.prettyPrint(out);
		
	}
	
	@Override public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new ProcessExecutorWrapper(this, probeNode);
	  }
}
