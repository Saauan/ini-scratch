package ini.ast.expression;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import ini.ast.Token;
import ini.ast.Visitor;
import ini.parser.IniParser;
import ini.runtime.IniException;

public abstract class AddNode extends BinaryNode {

	public AddNode(IniParser parser, Token token) {
		super(parser, token);
		// TODO Auto-generated constructor stub
	}
	
    /**
     * Specialization for primitive {@code long} values. This is the fast path of the
     * arbitrary-precision arithmetic. We need to check for overflows of the addition, and switch to
     * the {@link #add(SLBigNumber, SLBigNumber) slow path}. Therefore, we use an
     * {@link Math#addExact(long, long) addition method that throws an exception on overflow}. The
     * {@code rewriteOn} attribute on the {@link Specialization} annotation automatically triggers
     * the node rewriting on the exception.
     * <p>
     * In compiled code, {@link Math#addExact(long, long) addExact} is compiled to efficient machine
     * code that uses the processor's overflow flag. Therefore, this method is compiled to only two
     * machine code instructions on the fast path.
     * <p>
     * This specialization is automatically selected by the Truffle DSL if both the left and right
     * operand are {@code long} values.
     */
    @Specialization(rewriteOn = ArithmeticException.class)
    protected Number add(Number left, Number right) {
    	return addNumbers(left, right);
    }
   
    @TruffleBoundary
	public static Number addNumbers(Number a, Number b) {
	    if(a instanceof Double || b instanceof Double) {
	        return a.doubleValue() + b.doubleValue();
	    } else if(a instanceof Float || b instanceof Float) {
	        return a.floatValue() + b.floatValue();
	    } else if(a instanceof Long || b instanceof Long) {
	        return a.longValue() + b.longValue();
	    } else {
	        return a.intValue() + b.intValue();
	    }
	}

    /**
     * Specialization for String concatenation. The SL specification says that String concatenation
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

}
