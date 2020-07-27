package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.runtime.IniList;

public class ListExpression extends AstExpression implements Expression {

	//TODO TEST WITH LIST INTENSIVE BENCHMARK
//	@Children
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
		Object[] processedElements = new Object[elements.length];
		for(int i=0; i<elements.length; i++) {
			processedElements[i] = elements[i].executeGeneric(virtualFrame);
		}
		IniList res = new IniList(virtualFrame, elements);
		return res;
	}
	
}
