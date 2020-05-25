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
public class Assignment extends AstElement implements Statement, Expression {

	public VariableAccess assignee;
	public Expression assignment;
	private FrameSlot slot;

	@Deprecated
	public Assignment(IniParser parser, Token token, VariableAccess assignee, Expression assignment) {
		this(parser, token, assignee, assignment, null);
	}
	
	public Assignment(IniParser parser, Token token, VariableAccess assignee, Expression assignment, FrameSlot slot) {
		super(parser, token);
		this.assignee = assignee;
		this.assignee.setDeclaration(true);
		this.assignment = assignment;
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
	
    // TODO : Add more specialization 
    
    /**
     * Generic write method that works for all possible types.
     */
//    @Specialization
    protected Object write(VirtualFrame frame, Object assignmentValue) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);

        frame.setObject(getSlot(), assignmentValue);
        return assignmentValue;
    }
    
  
	@Override
	public void prettyPrint(PrintStream out) {
		assignee.prettyPrint(out);
		out.print("=");
		assignment.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAssignment(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
    	if(this.slot==null) {
    		this.slot = getSlotFromFrameDescriptor(frame.getFrameDescriptor());
    	}
		Object assignmentValue = assignment.executeGeneric(frame);
		this.write(frame, assignmentValue);
		return assignmentValue;
	}
	
}
