package ini.ast;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;

import ini.parser.IniParser;
import ini.runtime.IniFunction;

/**
 * Invocations share the same name as the function or process they invoke
 * 
 * If the function is not found within the current context,
 * searchs in the root context
 * 
 * 
 */
public class Invocation extends NamedElement implements Statement, Expression {

	@Children public AstElement[] argumentNodes;
	@Child protected IndirectCallNode callNode;

//	@Deprecated
//	public Invocation(IniParser parser, Token token, String name, List<Expression> arguments) {
//		this(parser,token,name,arguments);
//	}
	
	public Invocation(IniParser parser, Token token, String name, List<Expression> arguments) {
		super(parser, token, name);
		this.nodeTypeId = AstNode.INVOCATION;
		this.argumentNodes = arguments.toArray(new AstElement[0]);
		this.callNode = Truffle.getRuntime().createIndirectCallNode();
	}

	@Override
	public String toString() {
		return name + "(" + StringUtils.join(argumentNodes, ",") + ")";
	}

	@Override
	@Deprecated
	public void prettyPrint(PrintStream out) {
//		out.print(name + "(");
//		prettyPrintList(out, argumentsNodes, ",");
//		out.print(")");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitInvocation(this);
	}

	@Override
	@ExplodeLoop
	public Object executeGeneric(VirtualFrame virtualFrame) {
		IniFunction function = this.lookupFunction(virtualFrame, this.name, this.argumentNodes);
        /*
         * The number of arguments is constant for one invoke node. During compilation, the loop is
         * unrolled and the execute methods of all arguments are inlined. This is triggered by the
         * ExplodeLoop annotation on the method. The compiler assertion below illustrates that the
         * array length is really constant.
         */
        CompilerAsserts.compilationConstant(this.argumentNodes.length);

		Object[] argumentValues = new Object[this.argumentNodes.length+1];
		// The first element of the frame's argument is the lexical scope
		argumentValues[0] = function.getLexicalScope();
		for(int i=0; i<this.argumentNodes.length; i++) {
			argumentValues[i+1] = this.argumentNodes[i].executeGeneric(virtualFrame);
		}
		
		return call(virtualFrame, function.callTarget, argumentValues);
	}
	
	public IniFunction lookupFunction(VirtualFrame frame, String name, AstElement[] argumentNodes) {
		String identifier = AstElement.getFunctionIdentifier(name, argumentNodes.length);
		FrameSlot functionSlot = frame.getFrameDescriptor().findFrameSlot(identifier);
		IniFunction function;
		try {
			function = (IniFunction) frame.getObject(functionSlot);
			// If the function is not in the local context
			if(function==null) {
				Frame globalFrame = (Frame) frame.getArguments()[0];
				function = (IniFunction) globalFrame.getObject(functionSlot);
			}
		} catch (FrameSlotTypeException e) {
			throw new RuntimeException("FrameSlotTypeException : The slot was not an object type");
		}
		return function;
	}
	
    protected Object call(VirtualFrame virtualFrame, CallTarget callTarget,
            Object[] arguments) {
        return this.callNode.call(callTarget, arguments);
    }
}
