package ini.ast.expression;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniException;

@NodeInfo(shortName = "<=")
public abstract class LowerOrEqualNode extends BinaryNode {

	public LowerOrEqualNode() {
	}
    
    @Specialization
    protected boolean lowerOrEqual(char left, char right) {
    	return left <= right;
    }
    
    @Specialization
    protected boolean lowerOrEqual(byte left, byte right) {
    	return left <= right;
    }
    
    @Specialization
    protected boolean lowerOrEqual(int left, int right) {
    	return left <= right;
    }
    
    @Specialization
    protected boolean lowerOrEqual(long left, long right) {
    	return left <= right;
    }
    
    @Specialization
    protected boolean lowerOrEqual(float left, float right) {
    	return left <= right;
    }
    
    @Specialization
    protected boolean lowerOrEqual(double left, double right) {
    	return left <= right;
    }
    
    @Specialization
    protected boolean lowerOrEqual(String left, String right) {
    	return left.compareTo(right) <= 0;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Specialization(guards = "sameClasses(left, right)")
    protected boolean lowerOrEqual(Comparable left, Comparable right) {
        return left.compareTo(right) <= 0;
    }

    static boolean sameClasses(Object left, Object right) {
        return left.getClass() == right.getClass();
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }
    
    @Override
    public String getSymbol() {
    	return "<=";
    }

}
