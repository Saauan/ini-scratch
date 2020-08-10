package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;

import ini.ast.AstExpression;
import ini.ast.Executable;
import ini.ast.Visitor;

@GenerateNodeFactory()
@NodeChild(value = "arguments", type = AstExpression[].class)
@GenerateWrapper
public abstract class BuiltInExecutable extends Executable {
	
	public static String defaultName;
	
	public BuiltInExecutable() {
		super(defaultName, null);
	}
	
	@Override
	public void accept(Visitor visitor) {
		// ignore
	}
	
	@Override public WrapperNode createWrapper(ProbeNode probeNode) {
	    return new BuiltInExecutableWrapper(this, probeNode);
	  }
	
}
