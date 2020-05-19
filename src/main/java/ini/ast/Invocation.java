package ini.ast;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import ini.parser.IniParser;

/**
 * Invocations share the same name as the function or process they invoke
 * 
 * If the function is not found within the current context,
 * searchs in the root context
 * 
 * 
 */
public class Invocation extends NamedElement implements Statement, Expression {

	public List<Expression> arguments;
	@Child protected AstElement functionNode;
	@Children protected final AstElement[] argumentNodes;
	@Child protected IndirectCallNode callNode;

	@Deprecated
	public Invocation(IniParser parser, Token token, String name, List<Expression> arguments) {
		this(parser,token,name,null,null);
	}
	
	public Invocation(IniParser parser, Token token, String name, AstElement functionNode, AstElement[] argumentNodes) {
		super(parser, token, name);
		this.nodeTypeId = AstNode.INVOCATION;
		this.functionNode = functionNode;
		this.argumentNodes = argumentNodes;
		this.callNode = Truffle.getRuntime().createIndirectCallNode();
	}

	@Override
	public String toString() {
		return name + "(" + StringUtils.join(arguments, ",") + ")";
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + "(");
		prettyPrintList(out, arguments, ",");
		out.print(")");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitInvocation(this);
	}

	@Override
	@ExplodeLoop
	public Object executeGeneric(VirtualFrame virtualFrame) {
		Executable function = (Executable) this.functionNode.executeGeneric(virtualFrame);
		
        /*
         * The number of arguments is constant for one invoke node. During compilation, the loop is
         * unrolled and the execute methods of all arguments are inlined. This is triggered by the
         * ExplodeLoop annotation on the method. The compiler assertion below illustrates that the
         * array length is really constant.
         */
        CompilerAsserts.compilationConstant(this.argumentNodes.length);
		
		Object[] argumentValues = new Object[this.argumentNodes.length + 1];
		for(int i=0; i<this.argumentNodes.length; i++) {
			argumentValues[i] = this.argumentNodes[i].executeGeneric(virtualFrame);
		}
		return call(virtualFrame, function.callTarget, argumentValues);
	}
	
    protected Object call(VirtualFrame virtualFrame, CallTarget callTarget,
            Object[] arguments) {
        return this.callNode.call(callTarget, arguments);
    }
}
