package ini.ast.expression;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.Token;
import ini.parser.IniParser;
import ini.runtime.IniException;

@NodeInfo(shortName = "*")
public abstract class MulNode extends BinaryNode {

	public MulNode(IniParser parser, Token token) {
		super(parser, token);
		// TODO Auto-generated constructor stub
	}

	@Specialization(rewriteOn = ArithmeticException.class)
    protected int mul(int left, int right) {
    	return Math.multiplyExact(left,right) ;
    }
    
    @Specialization
    protected long mul(long left, long right) {
    	return left * right;
    }
    
    @Specialization
    protected float mul(float left, float right) {
    	return left * right;
    }
    
    @Specialization
    protected double mul(double left, double right) {
    	return left * right;
    }
    
    @Specialization
    protected byte mul(byte left, byte right) {
    	return (byte) (left * right); // TODO check if optimal
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }

}