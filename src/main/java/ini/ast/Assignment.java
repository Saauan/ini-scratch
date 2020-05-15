package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName="=")
public class Assignment extends AstElement implements Statement, Expression {

	public VariableAccess assignee;
	public Expression assignment;

	public Assignment(IniParser parser, Token token, VariableAccess assignee, Expression assignment) {
		super(parser, token);
		this.assignee = assignee;
		this.assignee.setDeclaration(true);
		this.assignment = assignment;
		this.nodeTypeId = AstNode.ASSIGNMENT;
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
	public Object execute(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
