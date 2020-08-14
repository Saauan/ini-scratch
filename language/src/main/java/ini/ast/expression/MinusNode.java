package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.AstExpression;
import ini.runtime.IniException;

@NodeChild("valueNode")
@NodeInfo(shortName = "-")
public abstract class MinusNode extends AstExpression {

	public MinusNode() {
	}
	
	@Specialization
    protected int minus(int value) {
        return -value;
    }
	
	@Specialization
    protected long minus(long value) {
        return -value;
    }
	
	@Specialization
    protected float minus(float value) {
        return -value;
    }
	
	@Specialization
    protected double minus(double value) {
        return -value;
    }

    @Fallback
    protected Object typeError(Object value) {
        throw IniException.typeError(this, value);
    }
    
	@Override
	public void prettyPrint(PrintStream out) {
		out.print("- [someNumber]");
	}

}
