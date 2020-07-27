package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import ini.runtime.IniException;
import ini.runtime.IniList;

public class ArrayAccess extends AstExpression implements VariableAccess {

	public AstExpression targetExpression;
	public AstExpression indexExpression;

	public ArrayAccess(AstExpression targetExpression, AstExpression indexExpression) {
		super();
		this.targetExpression = targetExpression;
		this.indexExpression = indexExpression;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		targetExpression.prettyPrint(out);
		out.print("[");
		indexExpression.prettyPrint(out);
		out.print("]");
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
		visitor.visitArrayAccess(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		IniList list = (IniList) targetExpression.executeGeneric(virtualFrame);
		int index;
		try {
			index = indexExpression.executeInteger(virtualFrame);
		} catch (UnexpectedResultException e) {
			throw new IniException("The index must be an integer !", this);
		}
		return list.getElementAt(index);
	}
}
