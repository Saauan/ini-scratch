package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.ast.AstExpression;
import ini.ast.Variable;
import ini.runtime.IniException;

/**
 * Things like -- or ++ that change the value and change the value in the frame
 */
public abstract class PostOperator extends UnaryNode {
	
	private final Operation operation;
	
	public PostOperator(Operation operation) {
		this.operation = operation;
	}
	
	protected Variable getVariableNode() {
		AstExpression valueNode = getValueNode();
		if(valueNode instanceof Variable) {
			return (Variable) valueNode;
		}
		else {
			throw IniException.typeError(this, valueNode);
		}
	}
	
	protected FrameSlot getVariableSlot(VirtualFrame frame) {
		return getVariableNode().getSlotSafe(frame);
	}
	
	public void prettyPrint(PrintStream out) {
		out.println("PostOperator");
	}
	
	public String getSymbol() {
		return this.operation.toString()+this.operation.toString();
	}
	
	@Specialization
    protected int postOp(VirtualFrame frame, int value) {
		int newValue = operation.doOp(value);
		updateValueInFrame(newValue, frame);
        return value;
    }
	
	@Specialization
    protected long postOp(VirtualFrame frame, long value) {
		long newValue = operation.doOp(value);
		updateValueInFrame(newValue, frame);
        return value;
    }
	
	@Specialization
    protected float postOp(VirtualFrame frame, float value) {
		float newValue = operation.doOp(value);
		updateValueInFrame(newValue, frame);
        return value;
    }
	
	@Specialization
    protected double postOp(VirtualFrame frame, double value) {
		double newValue = operation.doOp(value);
		updateValueInFrame(newValue, frame);
        return value;
    }
	
	@Specialization
    protected byte postOp(VirtualFrame frame, byte value) {
		byte newValue = operation.doOp(value);
		updateValueInFrame(newValue, frame);
        return value;
    }
	
	@Specialization
    protected boolean postOp(VirtualFrame frame, boolean value) {
		boolean newValue = operation.doOp(value);
		updateValueInFrame(newValue, frame);
        return value;
    }
	
//	@Specialization
//    protected Object postOp(VirtualFrame frame, Object value) {
//		Object newValue = operation.doOp(value);
//		updateValueInFrame(newValue, frame);
//        return value;
//    }

    @Fallback
    protected Object typeError(Object value) {
        throw IniException.typeError(this, value);
    }

	private void updateValueInFrame(int newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Int || kind == FrameSlotKind.Illegal;
		frame.setInt(slot, newValue);
	}
	
	private void updateValueInFrame(long newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
		frame.setLong(slot, newValue);
	}
	
	private void updateValueInFrame(float newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Float || kind == FrameSlotKind.Illegal;
		frame.setFloat(slot, newValue);
	}
	
	private void updateValueInFrame(double newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Double || kind == FrameSlotKind.Illegal;
		frame.setDouble(slot, newValue);
	}
	
	private void updateValueInFrame(byte newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Byte || kind == FrameSlotKind.Illegal;
		frame.setByte(slot, newValue);
	}
	
	private void updateValueInFrame(boolean newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Boolean || kind == FrameSlotKind.Illegal;
		frame.setBoolean(slot, newValue);
	}
	
	private void updateValueInFrame(Object newValue, VirtualFrame frame) {
		FrameSlot slot = getVariableSlot(frame);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		assert kind == FrameSlotKind.Object || kind == FrameSlotKind.Illegal;
		frame.setObject(slot, newValue);
	}
}
