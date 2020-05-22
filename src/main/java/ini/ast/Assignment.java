package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
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
	private final FrameSlot slot;

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
    	assert slot!=null;
    	return slot;
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
		Object assignmentValue = assignment.executeGeneric(frame);
		this.write(frame, assignmentValue);
		return assignmentValue;
	}
	
}
