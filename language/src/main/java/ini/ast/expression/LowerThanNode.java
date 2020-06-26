package ini.ast.expression;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniException;

//TODO use comparable interface ?
@NodeInfo(shortName = "<")
public abstract class LowerThanNode extends BinaryNode {

	public LowerThanNode() {
	}
    
    @Specialization
    protected boolean lowerThan(char left, char right) {
    	return left < right;
    }
    
    @Specialization
    protected boolean lowerThan(byte left, byte right) {
    	return left < right;
    }
    
    @Specialization
    protected boolean lowerThan(int left, int right) {
    	return left < right;
    }
    
    @Specialization
    protected boolean lowerThan(long left, long right) {
    	return left < right;
    }
    
    @Specialization
    protected boolean lowerThan(float left, float right) {
    	return left < right;
    }
    
    @Specialization
    protected boolean lowerThan(double left, double right) {
    	return left < right;
    }
    
    @Specialization
    protected boolean lowerThan(String left, String right) {
    	return left.compareTo(right) < 0;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Specialization(guards = "sameClasses(left, right)")
    protected boolean lowerThan(Comparable left, Comparable right) {
        return left.compareTo(right) < 0;
    }

    static boolean sameClasses(Object left, Object right) {
        return left.getClass() == right.getClass();
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }

}
