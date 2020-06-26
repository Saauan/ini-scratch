package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ConstructorMatchExpression extends NamedElement implements Expression {

	public List<Expression> fieldMatchExpressions;
	public TypeVariable type;

	public ConstructorMatchExpression(String name,
			List<Expression> fieldMatchExpressions) {
		super(name);
		this.fieldMatchExpressions = fieldMatchExpressions;
	}

	public ConstructorMatchExpression(TypeVariable type) {
		super(type.name);
		this.type = type;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (type != null) {
			type.prettyPrint(out);
		} else {
			out.print(name);
			if (fieldMatchExpressions != null && !fieldMatchExpressions.isEmpty()) {
				out.print("[");
				prettyPrintList(out, fieldMatchExpressions, ",");
				out.print("]");
			}
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitConstructorMatchExpression(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}

}
