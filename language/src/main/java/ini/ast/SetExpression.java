package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SetExpression extends AstExpression implements Expression {

	public List<Variable> variables;
	public Expression set;
	public Expression expression;
	
	public SetExpression(List<Variable> variables, Expression set, Expression expression) {
		super();
		this.variables = variables;
		for(Variable v : variables) {
			v.setDeclaration(true);
		}
		this.set = set;
		this.expression = expression;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		prettyPrintList(out, variables, ",");
		out.print(" of "+set+" | ");
		expression.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitSetExpression(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
