package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import ini.runtime.IniException;
import ini.runtime.IniList;

/**
 * When we assign a value (assignment) to something else (assignee)
 * 
 * The NodeChild annotation signifies to the Truffle DSL that the Assignment has
 * a NodeChild which when executed returns a result named assignmentValue.
 * 
 * The execution of the Node described in NodeChild happens in the generated
 * class AssignmentNodeGen.
 */
@NodeInfo(shortName = "=")
@NodeChild("assignmentValue")
public abstract class Assignment extends AstExpression implements Statement, Expression {

	/* The Node to which we assign the value */
	@Child
	public AstElement assignee;
	/* The slot is final for the compiler only */
	@CompilationFinal
	private FrameSlot slot;

	@Deprecated
	public Assignment(VariableAccess assignee) {
		this(assignee, null);
	}

	public Assignment(VariableAccess assignee, FrameSlot slot) {
		super();
		this.assignee = (AstElement) assignee;
		this.slot = slot;
	}

	/**
	 * Returns the descriptor of the accessed local variable. The implementation of
	 * this method is created by the Truffle DSL based on the {@link NodeField}
	 * annotation on the class.
	 */
	protected FrameSlot getSlot() {
		return this.slot;
	}

	protected FrameSlot getSlotSafe(VirtualFrame frame) {
		if (this.slot == null) {
			initializeSlot(frame);
		}
		return this.slot;
	}

	private void initializeSlot(VirtualFrame frame) {
		/*
		 * We are about to modify a CompilationFinal field. So we tell the Compiler to
		 * transfer to interpreter and invalidate the following code
		 */
		CompilerDirectives.transferToInterpreterAndInvalidate();
		this.slot = getSlotFromFrameDescriptor(frame.getFrameDescriptor());
	}

	protected FrameSlot getSlotFromFrameDescriptor(FrameDescriptor frameDescriptor) {
		if (assignee instanceof Variable) {
			CompilerDirectives.transferToInterpreter();
			return frameDescriptor.findOrAddFrameSlot(((Variable) assignee).name);
		}
//		else if (assignee instanceof ArrayAccess) {
//			CompilerDirectives.transferToInterpreter();
//			return frameDescriptor.findOrAddFrameSlot();
//		}
		throw new RuntimeException("Can't get a frameSlot from something that is not a variable (not implemented)");
	}

	@Specialization(guards = "assigneeIsList()")
	protected Object writeInList(VirtualFrame frame, Object value) {
		int index;
		assert assignee instanceof ArrayAccess;
		ArrayAccess access = (ArrayAccess) assignee;
		try {
			index = access.indexExpression.executeInteger(frame);
		} catch (UnexpectedResultException e) {
			throw new IniException("The value of the index must be an integer.", this);
		}
		IniList theList = (IniList) access.targetExpression.executeGeneric(frame);
		theList.setElementAt(index, value);
		
		return value;
	}
	
	/**
	 * Specialized method to write a primitive {@code int} value. This is only
	 * possible if the local variable also has currently the type {@code int} or was
	 * never written before, therefore a Truffle DSL
	 * {@link #isIntegerOrIllegal(VirtualFrame) custom guard} is specified.
	 */
	@Specialization(guards = "isIntOrIllegal(frame)")
	protected long writeInt(VirtualFrame frame, int value) {
		/*
		 * Initialize type on first write of the local variable. No-op if kind is
		 * already int.
		 */
		frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Int);

		frame.setInt(getSlot(), value);
		return value;
	}

	@Specialization(guards = "isLongOrIllegal(frame)")
	protected long writeLong(VirtualFrame frame, long value) {
		/*
		 * Initialize type on first write of the local variable. No-op if kind is
		 * already Long.
		 */
		frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Long);

		frame.setLong(getSlot(), value);
		return value;
	}

	@Specialization(guards = "isFloatOrIllegal(frame)")
	protected float writeFloat(VirtualFrame frame, float value) {
		/*
		 * Initialize type on first write of the local variable. No-op if kind is
		 * already Long.
		 */
		frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Float);

		frame.setFloat(getSlot(), value);
		return value;
	}

	@Specialization(guards = "isDoubleOrIllegal(frame)")
	protected double writeDouble(VirtualFrame frame, double value) {
		/*
		 * Initialize type on first write of the local variable. No-op if kind is
		 * already Long.
		 */
		frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Double);

		frame.setDouble(getSlot(), value);
		return value;
	}

	@Specialization(guards = "isBooleanOrIllegal(frame)")
	protected boolean writeBoolean(VirtualFrame frame, boolean value) {
		/*
		 * Initialize type on first write of the local variable. No-op if kind is
		 * already Long.
		 */
		frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Boolean);

		frame.setBoolean(getSlot(), value);
		return value;
	}

	@Specialization(guards = "isByteOrIllegal(frame)")
	protected byte writeByte(VirtualFrame frame, byte value) {
		/*
		 * Initialize type on first write of the local variable. No-op if kind is
		 * already Long.
		 */
		frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Byte);

		frame.setByte(getSlot(), value);
		return value;
	}

	/**
	 * Generic write method that works for all possible types.
	 * <p>
	 * Why is this method annotated with {@link Specialization} and not
	 * {@link Fallback}? For a {@link Fallback} method, the Truffle DSL generated
	 * code would try all other specializations first before calling this method. We
	 * know that all these specializations would fail their guards, so there is no
	 * point in calling them. Since this method takes a value of type
	 * {@link Object}, it is guaranteed to never fail, i.e., once we are in this
	 * specialization the node will never be re-specialized.
	 */
	@Specialization(replaces = { "writeInt", "writeLong", "writeBoolean", "writeFloat", "writeDouble", "writeByte"})
	protected Object write(VirtualFrame frame, Object assignmentValue) {
		/*
		 * Regardless of the type before, the new and final type of the local variable
		 * is Object. Changing the slot kind also discards compiled code, because the
		 * variable type is important when the compiler optimizes a method.
		 *
		 * No-op if kind is already Object.
		 */
		assert !(this.assignee instanceof ArrayAccess); 
		frame.getFrameDescriptor().setFrameSlotKind(getSlotSafe(frame), FrameSlotKind.Object);

		frame.setObject(getSlot(), assignmentValue);
		return assignmentValue;
	}

	protected boolean assigneeIsList() {
		return this.assignee instanceof ArrayAccess;
	}
	
	/**
	 * Guard function that the local variable has the type {@code long}.
	 *
	 * @param frame The parameter seems unnecessary, but it is required: Without the
	 *              parameter, the Truffle DSL would not check the guard on every
	 *              execution of the specialization. Guards without parameters are
	 *              assumed to be pure, but our guard depends on the slot kind which
	 *              can change.
	 */
	protected boolean isLongOrIllegal(VirtualFrame frame) {
		final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlotSafe(frame));
		return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
	}

	protected boolean isIntOrIllegal(VirtualFrame frame) {
		final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlotSafe(frame));
		return kind == FrameSlotKind.Int || kind == FrameSlotKind.Illegal;
	}

	protected boolean isFloatOrIllegal(VirtualFrame frame) {
		final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlotSafe(frame));
		return kind == FrameSlotKind.Float || kind == FrameSlotKind.Illegal;
	}

	protected boolean isDoubleOrIllegal(VirtualFrame frame) {
		final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlotSafe(frame));
		return kind == FrameSlotKind.Double || kind == FrameSlotKind.Illegal;
	}

	protected boolean isByteOrIllegal(VirtualFrame frame) {
		final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlotSafe(frame));
		return kind == FrameSlotKind.Byte || kind == FrameSlotKind.Illegal;
	}

	protected boolean isBooleanOrIllegal(VirtualFrame frame) {
		final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlotSafe(frame));
		return kind == FrameSlotKind.Boolean || kind == FrameSlotKind.Illegal;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		assignee.prettyPrint(out);
		out.print("=");
		/*
		 * I can't use the assignment field as it is constrained by the NodeChild
		 * annotation. I would only be able to use its value, if I pass it as a
		 * parameter of prettyPrint
		 */
//		assignment.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAssignment(this);
	}
}
