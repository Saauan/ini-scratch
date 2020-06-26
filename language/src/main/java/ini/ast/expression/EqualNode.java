package ini.ast.expression;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniException;

@NodeInfo(shortName = "==")
public abstract class EqualNode extends BinaryNode {

	public EqualNode() {
	}
	
    @Specialization
    protected boolean equal(boolean left, boolean right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(char left, char right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(byte left, byte right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(int left, int right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(long left, long right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(float left, float right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(double left, double right) {
    	return left == right;
    }
    
    @Specialization
    protected boolean equal(String left, String right) {
    	return left.equals(right);
    }

    /**
     * We covered all the cases that can return true in the type specializations above. If we
     * compare two values with different types, the result is known to be false.
     * <p>
     * Note that the guard is essential for correctness: without the guard, the specialization would
     * also match when the left and right value have the same type. The following scenario would
     * return a wrong value: First, the node is executed with the left value 42 (type Number) and the
     * right value "abc" (String). This specialization matches, and since it is the first execution
     * it is also the only specialization. Then, the node is executed with the left value "42" (type
     * Number) and the right value "42" (type Number). Since this specialization is already present, and
     * (without the guard) also matches (Number are children of Object), it is executed. The
     * wrong return value is "false".
     */
    @Specialization(guards = "differentClasses(left, right)")
    protected boolean equal(Object left, Object right) {
        assert !left.equals(right);
        return false;
    }

    static boolean differentClasses(Object left, Object right) {
        return left.getClass() != right.getClass();
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }

}
