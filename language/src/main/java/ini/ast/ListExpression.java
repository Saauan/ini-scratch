package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ListExpression extends AstExpression implements Expression {

	public List<Expression> elements;
	
	public ListExpression(List<Expression> elements) {
		super();
		this.elements = elements;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("[");
		for(int i=0;i<elements.size();i++) {
			elements.get(i).prettyPrint(out);
			if(i<elements.size()-1) {
				out.print(",");
			}
		}
		out.print("]");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitListExpression(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
