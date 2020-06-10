package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

public class SetConstructor extends AstExpression implements Expression {

	public List<Assignment> fieldAssignments;
	public String name;

	public SetConstructor(IniParser parser, Token token, String name, List<Assignment> fieldAssignments) {
		super(parser, token);
		this.name = name;
		this.fieldAssignments = fieldAssignments;
		this.nodeTypeId = AstNode.SET_CONSTRUCTOR;
	}

	public Expression getFieldExpression(String fieldName) {
		for (Assignment a : fieldAssignments) {
			if (fieldName.equals(a.assignee.toString())) {
				return null;
//			    return a.assignment; // TODO 
			}
		}
		return null;
	}

	public boolean hasField(String fieldName) {
		for (Assignment a : fieldAssignments) {
			if (fieldName.equals(a.assignee.toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + "[");
		prettyPrintList(out, fieldAssignments, ",");
		out.print("]");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitSetConstructor(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
}
