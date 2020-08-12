package ini.ast;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oracle.truffle.api.frame.VirtualFrame;

/* Represents a future return value from a Process 
 * 
 * When the creating Process ends it stores its return value in this object using setReturnValue 
 * 
 * When executeGeneric is invoked, it checks if the process has ended, if not,
 * waits for it to end and set the return value */
public class ProcessReturnValue extends AstExpression implements Future<Object>{

	private Object returnValue = null;
	private boolean isReturnValueSet = false;
	private CountDownLatch setSignal = new CountDownLatch(1);

	@Override
	public void prettyPrint(PrintStream out) {
	}

	public void setReturnValue(Object value) {
		if (isReturnValueSet) {
			throw new IllegalStateException("You can't change the return value if it is already set");
		}
		this.returnValue = value;
		this.isReturnValueSet = true;
		this.setSignal.countDown();
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		if (!isReturnValueSet) {
			try {
				setSignal.await();
			} catch (InterruptedException e) {
				// The current thread's been interrupted, must return as quickly as possible
				Thread.currentThread().interrupt();
				System.err.println("WARN : Returning without waiting for process to return");
				return null;
			}
			assert isReturnValueSet;
		}
		return this.returnValue;
	}

	@Override
	public boolean cancel(boolean arg0) {
		return false;
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		setSignal.await();
		assert isReturnValueSet;
		return this.returnValue;
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		setSignal.await(timeout, unit);
		assert isReturnValueSet;
		return this.returnValue;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return this.isReturnValueSet;
	}

}
