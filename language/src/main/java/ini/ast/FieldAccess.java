package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class FieldAccess extends AstElement implements VariableAccess {

	public Expression targetExpression;
	public String fieldName;

	public FieldAccess(Expression targetExpression, String fieldName) {
		super();
		this.targetExpression = targetExpression;
		this.fieldName = fieldName;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		targetExpression.prettyPrint(out);
		out.print("." + fieldName);
	}

	@Override
	public boolean isDeclaration() {
		if (targetExpression instanceof VariableAccess) {
			return ((VariableAccess) targetExpression).isDeclaration();
		} else {
			return false;
		}
	}

	@Override
	public void setDeclaration(boolean declaration) {
		if (targetExpression instanceof VariableAccess) {
			((VariableAccess) targetExpression).setDeclaration(declaration);
		} else {
			// ignore
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitFieldAccess(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}
	
}
