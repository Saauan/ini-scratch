package ini.ast.expression;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.Token;
import ini.parser.IniParser;
import ini.runtime.IniException;

@NodeInfo(shortName = "-")
public abstract class SubNode extends BinaryNode {

	public SubNode(IniParser parser, Token token) {
		super(parser, token);
		// TODO Auto-generated constructor stub
	}
	
	@Specialization
    protected int sub(int left, int right) {
    	return left - right ;
    }
    
    @Specialization
    protected long sub(long left, long right) {
    	return left - right;
    }
    
    @Specialization
    protected float sub(float left, float right) {
    	return left - right;
    }
    
    @Specialization
    protected double sub(double left, double right) {
    	return left - right;
    }
    
    @Specialization
    protected byte sub(byte left, byte right) {
    	return (byte) (left - right); // TODO check if optimal
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
    	throw IniException.typeError(this, left, right);
    }

}
