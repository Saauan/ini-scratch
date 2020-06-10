package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 * When we assign a value (assignment) to something else (assignee)
 */
@NodeInfo(shortName="=")
@NodeChild("assignmentValue")
public abstract class Assignment extends AstExpression implements Statement, Expression {

	// VariableAccess TODO
	@Child public AstElement assignee;
	// Expression TODO
	@Child public AstElement assignment;
	private FrameSlot slot;

	@Deprecated
	public Assignment(IniParser parser, Token token, VariableAccess assignee) {
		this(parser, token, assignee, null);
	}
	
	public Assignment(IniParser parser, Token token, VariableAccess assignee, FrameSlot slot) {
		super(parser, token);
		this.assignee = (AstElement) assignee;
//		this.assignee.setDeclaration(true); TODO
//		this.assignment = (AstElement) assignmentNode;
		this.nodeTypeId = AstNode.ASSIGNMENT;
		this.slot = slot;
	}

    /**
     * Returns the descriptor of the accessed local variable. The implementation of this method is
     * created by the Truffle DSL based on the {@link NodeField} annotation on the class.
     */
    protected FrameSlot getSlot() {
    	assert this.slot!=null : "The slot was null";
    	return this.slot;
    }
    
    protected FrameSlot getSlotFromFrameDescriptor(FrameDescriptor frameDescriptor) {
    	if (assignee instanceof Variable) {
    		return frameDescriptor.findOrAddFrameSlot(((Variable) assignee).name);
    	}
    	throw new RuntimeException("Can't get a frameSlot from something that is not a variable (not implemented)");
    }
	
    
    /**
     * Generic write method that works for all possible types.
     */
    @Specialization
    protected Object write(VirtualFrame frame, Object assignmentValue) {
    	checkSlot(frame);
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);

        frame.setObject(getSlot(), assignmentValue);
        return assignmentValue;
    }

	private void checkSlot(VirtualFrame frame) {
		if(this.slot==null) {
    		this.slot = getSlotFromFrameDescriptor(frame.getFrameDescriptor());
    	}
	}
    
  
	@Override
	public void prettyPrint(PrintStream out) {
		assignee.prettyPrint(out);
		out.print("=");
//		assignment.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAssignment(this);
	}

//	@Override
//	public Object executeGeneric(VirtualFrame frame) {
//		Object assignmentValue = assignment.executeGeneric(frame);
//		this.write(frame, assignmentValue);
//		return assignmentValue;
//    	return null;
//	}
//	
}
