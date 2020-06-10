package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * When executed, returns the value of the variable (value access)
 */
public class Variable extends AstExpression implements VariableAccess {

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
	
    /**
     * Returns the descriptor of the accessed local variable. */
    protected FrameSlot getSlot() {
    	return slot;
    }
    
    protected FrameSlot getSlotFromFrameDescriptor(FrameDescriptor frameDescriptor) {
    	return frameDescriptor.findFrameSlot(name);
    }
    
    // BUG : Might have bug because char and boolean are not objects
    // TODO : more specialization
    
    protected Object readObject(VirtualFrame frame) {
    	if (this.slot == null) {
    		this.slot = getSlotFromFrameDescriptor(frame.getFrameDescriptor());
    		if (this.slot == null) {
    			throw new RuntimeException(String.format("Variable %s not found", name));
    		}
    	}
        if (!frame.isObject(getSlot())) {
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
            return result;
        }
        Object readObject = FrameUtil.getObjectSafe(frame, getSlot());
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

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		return readObject(virtualFrame);
	}
	
}
