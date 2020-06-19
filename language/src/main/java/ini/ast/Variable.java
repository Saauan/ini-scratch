package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

/**
 * When executed, returns the value of the variable (value access)
 */
public abstract class Variable extends AstExpression implements VariableAccess {

	private boolean declaration = false;
	private FrameSlot slot;
	public String name;

	/**
	 * If this variable is an access to a global channel declaration, it will
	 * return the channel after the attribution phase (null otherwise or before
	 * the attribution phase).
	 */
	public ChannelDeclaration channelLiteral;

	@Deprecated
	public Variable(IniParser parser, Token token, String name) {
		this(parser, token, name, null);
	}
	
	public Variable(IniParser parser, Token token, String name, FrameSlot slot) {
		super(parser, token);
		this.name = name;
		this.nodeTypeId = AstNode.VARIABLE;
		this.slot = slot;
	}
	
	protected FrameSlot getSlot() {
		return this.slot;
	}
	
    /**
     * Returns the descriptor of the accessed local variable. */
    protected FrameSlot getSlotSafe(VirtualFrame frame) {
    	if(this.slot == null) {
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
        return FrameUtil.getLongSafe(frame, getSlotSafe(frame));
    }
    
    @Specialization(guards = "frame.isInt(getSlotSafe(frame))")
    protected int readInt(VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, getSlotSafe(frame));
    }
    
    @Specialization(guards = "frame.isFloat(getSlotSafe(frame))")
    protected float readFloat(VirtualFrame frame) {
        return FrameUtil.getFloatSafe(frame, getSlotSafe(frame));
    }
    
    @Specialization(guards = "frame.isDouble(getSlotSafe(frame))")
    protected double readDouble(VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, getSlotSafe(frame));
    }
    
    @Specialization(guards = "frame.isByte(getSlotSafe(frame))")
    protected Byte readByte(VirtualFrame frame) {
        return FrameUtil.getByteSafe(frame, getSlotSafe(frame));
    }

    @Specialization(guards = "frame.isBoolean(getSlotSafe(frame))")
    protected boolean readBoolean(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, getSlotSafe(frame));
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
            Object result = frame.getValue(getSlotSafe(frame));
            frame.setObject(getSlotSafe(frame), result);
            return result;
        }
        Object readObject = FrameUtil.getObjectSafe(frame, getSlotSafe(frame));
        if (readObject == null) {
        	System.out.println(String.format("Warning : %s value is null", this.name)); // TODO remove eventually
        }
        return readObject;
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

//	@Override
//	public Object executeGeneric(VirtualFrame virtualFrame) {
//		return readObject(virtualFrame);
//	}
	
}