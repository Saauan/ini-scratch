package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Parameter extends NamedElement {

	public Expression defaultValue;
	
	public Parameter(String name, Expression defaultValue) {
		super(name);
		this.defaultValue = defaultValue;
	}

	public Parameter(String name) {
		this(name,null);
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name);
		if(defaultValue!=null) {
			out.print("=");
			defaultValue.prettyPrint(out);
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitParameter(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}
	
}
