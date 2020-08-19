package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.AstExpression;
import ini.runtime.IniException;

@NodeInfo(shortName = "!")
public abstract class LogicalNotNode extends UnaryNode {

	public LogicalNotNode() {
	}
	
	public String getSymbol() {
		return "!";
	}
	
	@Specialization
    protected boolean equal(boolean value) {
        return !value;
    }

    @Fallback
    protected Object typeError(Object value) {
        throw IniException.typeError(this, value);
    }
    
	@Override
	public void prettyPrint(PrintStream out) {
		out.print("! [SomeCondition]");
	}

}
