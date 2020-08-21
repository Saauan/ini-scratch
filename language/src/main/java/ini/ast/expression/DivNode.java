package ini.ast.expression;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniException;

@NodeInfo(shortName = "/")
public abstract class DivNode extends BinaryNode {

	public DivNode() {
	}

	@Specialization
    protected int div(int left, int right) {
    	return left / right ;
    }
    
    @Specialization
    protected long div(long left, long right) {
    	return left / right;
    }
    
    @Specialization
    protected float div(float left, float right) {
    	return left / right;
    }
    
    @Specialization
    protected double div(double left, double right) {
    	return left / right;
    }
    
    @Specialization
    protected byte div(byte left, byte right) {
    	return (byte) (left / right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }

    @Override
    public String getSymbol() {
    	return "/";
    }
}
