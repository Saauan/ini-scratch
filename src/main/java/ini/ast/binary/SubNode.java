package ini.ast.binary;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

import ini.ast.Token;
import ini.ast.Visitor;
import ini.parser.IniParser;

public abstract class SubNode extends BinaryNode {

	public SubNode(IniParser parser, Token token) {
		super(parser, token);
		// TODO Auto-generated constructor stub
	}
	
    @Specialization
    protected Number sub(Number left, Number right) {
    	return subNumbers(left, right);
    }
   
    @TruffleBoundary
	public static Number subNumbers(Number a, Number b) {
	    if(a instanceof Double || b instanceof Double) {
	        return a.doubleValue() - b.doubleValue();
	    } else if(a instanceof Float || b instanceof Float) {
	        return a.floatValue() - b.floatValue();
	    } else if(a instanceof Long || b instanceof Long) {
	        return a.longValue() - b.longValue();
	    } else {
	        return a.intValue() - b.intValue();
	    }
	}

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw new RuntimeException(String.format("%s %s %s are not compatible for substraction", this, left, right));
    }

}