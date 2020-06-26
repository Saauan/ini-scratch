package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;

import ini.ast.AstExpression;
import ini.ast.Executable;
import ini.ast.Visitor;

@GenerateNodeFactory()
@NodeChild(value = "arguments", type = AstExpression[].class)
public abstract class BuiltInExecutable extends Executable {
	
	public static String defaultName;
	
	public BuiltInExecutable() {
		super(defaultName, null);
	}
	
	@Override
	public void accept(Visitor visitor) {
		// ignore
	}
	
}
