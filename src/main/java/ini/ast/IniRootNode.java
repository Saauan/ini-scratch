package ini.ast;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import ini.IniLanguage;

public class IniRootNode extends RootNode {

//	@Children
	private final AstElement[] bodyNodes;

	public IniRootNode(IniLanguage language, AstElement[] bodyNodes, FrameDescriptor frameDescriptor) {
		super(language, frameDescriptor);
		this.bodyNodes = bodyNodes;
	}

	@Override
	@ExplodeLoop
	public Object execute(VirtualFrame frame) {
		AstElement[] s = this.bodyNodes;
		int nbNodes = s.length;
		CompilerAsserts.compilationConstant(nbNodes);
		int i;
		for (i = 0; i < nbNodes-1; i++) {
			s[i].executeGeneric(frame);
		}
		return s[i].executeGeneric(frame);
	}

	public static IniRootNode create(IniLanguage lang, FrameSlot[] parametersSlots, AstElement[] bodyNodes,
			FrameDescriptor frameDescriptor) {
		AstElement[] allNodes = new AstElement[parametersSlots.length+bodyNodes.length];
		// Insert all parameters
		if(parametersSlots.length>0) {
			for (int arg_index = 0; arg_index < parametersSlots.length; arg_index--) {
				allNodes[arg_index] = createAssignment(parametersSlots, arg_index);
			}
		}
		System.arraycopy(bodyNodes, 0, allNodes,
                parametersSlots.length, bodyNodes.length);
		return new IniRootNode(lang, allNodes, frameDescriptor);
	}

	/**
	 * Creates an assignment to read the argument given at function invocation and
	 * writes it in the frame
	 */
	private static Assignment createAssignment(FrameSlot[] argumentNames, int arg_index) {
		return new Assignment(null, null,
				new Variable(null, null, argumentNames[arg_index].toString(), argumentNames[arg_index]),
				new ReadArgumentFromContextNode(null, null, arg_index), argumentNames[arg_index]);
	}

	@Deprecated
	public void setName(String string) {
		// TODO Auto-generated method stub
		
	}

}
