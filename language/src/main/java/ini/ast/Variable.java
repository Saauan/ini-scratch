package ini.ast;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * When executed, returns the value of the variable
 */
public abstract class Variable extends AstExpression implements VariableAccess {

	private boolean declaration = false;
	@CompilationFinal private FrameSlot slot;
	public String name;

	/**
	 * If this variable is an access to a global channel declaration, it will
	 * return the channel after the attribution phase (null otherwise or before
	 * the attribution phase).
	 */
	public ChannelDeclaration channelLiteral;

	@Deprecated
	public Variable(String name) {
		this(name, null);
	}
	
	public Variable(String name, FrameSlot slot) {
		super();
		this.name = name;
		this.slot = slot;
	}
	
	protected FrameSlot getSlot() {
		return this.slot;
	}
	
    /**
     * Returns the descriptor of the accessed local variable. */
    protected FrameSlot getSlotSafe(VirtualFrame frame) {
    	if(this.slot == null) {
    		CompilerDirectives.transferToInterpreterAndInvalidate();
    		initializeSlot(frame);
    	}
    	return this.slot;
    }
    
	private void initializeSlot(VirtualFrame frame) {
		this.slot = getSlotFromFrameDescriptor(frame.getFrameDescriptor());
		if (this.slot == null) {
			throw new RuntimeException(String.format("Variable %s not found", name));
		}
	}
    
    protected FrameSlot getSlotFromFrameDescriptor(FrameDescriptor frameDescriptor) {
    	return frameDescriptor.findFrameSlot(name);
    }
    
    @Specialization(guards = "frame.isLong(getSlotSafe(frame))")
    protected long readLong(VirtualFrame frame) {
        /*
         * When the FrameSlotKind is Long, we know that only primitive long values have ever been
         * written to the local variable. So we do not need to check that the frame really contains
         * a primitive long value.
         */
        return FrameUtil.getLongSafe(frame, getSlot());
    }
    
    @Specialization(guards = "frame.isInt(getSlotSafe(frame))")
    protected int readInt(VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, getSlot());
    }
    
    @Specialization(guards = "frame.isFloat(getSlotSafe(frame))")
    protected float readFloat(VirtualFrame frame) {
        return FrameUtil.getFloatSafe(frame, getSlot());
    }
    
    @Specialization(guards = "frame.isDouble(getSlotSafe(frame))")
    protected double readDouble(VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, getSlot());
    }
    
    @Specialization(guards = "frame.isByte(getSlotSafe(frame))")
    protected Byte readByte(VirtualFrame frame) {
        return FrameUtil.getByteSafe(frame, getSlot());
    }

    @Specialization(guards = "frame.isBoolean(getSlotSafe(frame))")
    protected boolean readBoolean(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, getSlot());
    }
    
    @Specialization(replaces = {"readInt", "readLong", "readFloat", "readDouble", "readByte", "readBoolean"})
    protected Object readObject(VirtualFrame frame) {
        if (!frame.isObject(getSlotSafe(frame))) {
            /*
             * The FrameSlotKind has been set to Object, so from now on all writes to the local
             * variable will be Object writes. However, now we are in a frame that still has an old
             * non-Object value. This is a slow-path operation: we read the non-Object value, and
             * write it immediately as an Object value so that we do not hit this path again
             * multiple times for the same variable of the same frame.
             */
            CompilerDirectives.transferToInterpreter();
            Object result = frame.getValue(getSlot());
            frame.setObject(getSlot(), result);
            result = checkFutureData(result);
            return result;
        }
        Object readObject = FrameUtil.getObjectSafe(frame, getSlot());
        if (readObject == null) {
        	System.err.println(String.format("Warning : %s value is null", this.name)); // TODO remove eventually
        }
        readObject = checkFutureData(readObject);
        return readObject;
    }
    
    /**
     * If the result a FutureData, it is unwrapped and return, otherwise does nothing
     * 
     * @param result
     * @return
     */
	@SuppressWarnings("unchecked")
	private Object checkFutureData(Object result) {
		if (result instanceof Future) {
			try {
				result = ((Future<Object>) result).get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				// I don't know what to do here
			}
		}
		return result;
	}

	public final void setDeclaration(boolean declaration) {
		this.declaration = declaration;
	}

	public final boolean isDeclaration() {
		return declaration;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitVariable(this);
	}
	
}
