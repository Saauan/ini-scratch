package ini.ast.expression;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniException;

@NodeInfo(shortName = "+")
public abstract class AddNode extends BinaryNode {

	public AddNode() {
		super();
	}
	
    /**
     * Specialization for primitive {@code int} values. This is the fast path of the
     * arbitrary-precision arithmetic. We need to check for overflows of the addition, and switch to
     * the {@link #add(long, long) slow path}. Therefore, we use an
     * {@link Math#addExact(int, int) addition method that throws an exception on overflow}. The
     * {@code rewriteOn} attribute on the {@link Specialization} annotation automatically triggers
     * the node rewriting on the exception.
     * <p>
     * In compiled code, {@link Math#addExact(int, int) addExact} is compiled to efficient machine
     * code that uses the processor's overflow flag. Therefore, this method is compiled to only two
     * machine code instructions on the fast path.
     * <p>
     * This specialization is automatically selected by the Truffle DSL if both the left and right
     * operand are {@code int} values.
     */
    @Specialization(rewriteOn = ArithmeticException.class)
    protected int add(int left, int right) {
    	return Math.addExact(left, right);
    }
    
    @Specialization
    protected long add(long left, long right) {
    	return Math.addExact(left, right);
    }
    
    @Specialization
    protected float add(float left, float right) {
    	return left + right;
    }
    
    @Specialization
    protected double add(double left, double right) {
    	return left + right;
    }
    
    @Specialization
    protected byte add(byte left, byte right) {
    	return (byte) (left + right);
    }

    /**
     * Specialization for String concatenation. The INI specification says that String concatenation
     * works if either the left or the right operand is a String. The non-string operand is
     * converted then automatically converted to a String.
     * <p>
     * To implement these semantics, we tell the Truffle DSL to use a custom guard. The guard
     * function is defined in {@link #isString this class}, but could also be in any superclass.
     */
    @Specialization(guards = "isString(left, right)")
    @TruffleBoundary
    protected String add(Object left, Object right) {
        return left.toString() + right.toString();
    }

    /**
     * Guard for String concatenation: returns true if either the left or the right operand is a
     * {@link String}.
     */
    protected boolean isString(Object a, Object b) {
        return a instanceof String || b instanceof String;
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }

    @Override
    public String getSymbol() {
    	return "+";
    }

}
