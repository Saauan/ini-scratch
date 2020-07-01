package ini.ast;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ListExpression extends AstExpression implements Expression {

	public AstExpression[] elements;
	
	public ListExpression(List<AstExpression> elements) {
		super();
		this.elements = elements.toArray(new AstExpression[0]);
		
	}

	@Override
	public void prettyPrint(PrintStream out) {
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitListExpression(this);
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		
		Map<Integer, Object> res = new HashMap<Integer, Object>();
		final int nbElements = elements.length;
		for (int i=0; i<nbElements; i++) {
			res.put(i, elements[i]);
		}
		return res;
	}
	
}
