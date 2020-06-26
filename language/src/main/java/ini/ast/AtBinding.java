package ini.ast;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

public class AtBinding extends NamedElement {

	public String className;
	public List<TypeVariable> configurationTypes;
	public List<TypeVariable> runtimeTypes;

	public AtBinding(String name,
			List<TypeVariable> configurationTypes, List<TypeVariable> runtimeTypes, String className) {
		super(name);
		this.configurationTypes = configurationTypes;
		this.runtimeTypes = runtimeTypes;
		this.className = className;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + " => " + "\"" + className + "\"");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAtBinding(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}
	
}
