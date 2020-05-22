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
	private final Sequence<AstElement> bodyNodes;

	public IniRootNode(IniLanguage language, Sequence<AstElement> bodyNodes, FrameDescriptor frameDescriptor) {
		super(language, frameDescriptor);
		this.bodyNodes = bodyNodes;
	}

	@Override
	@ExplodeLoop
	public Object execute(VirtualFrame frame) {
		Sequence<AstElement> s = this.bodyNodes;
		int nbNodes = s.size() - 1;
		CompilerAsserts.compilationConstant(nbNodes);
		for (int i = 0; i < nbNodes - 1; i++) {
			s.get().executeGeneric(frame);
			s = s.next();
		}
		return s.get().executeGeneric(frame);
	}

	public static IniRootNode create(IniLanguage lang, FrameSlot[] argumentNames, Sequence<AstElement> bodyNodes,
			FrameDescriptor frameDescriptor) {
		Sequence<AstElement> allNodes = new Sequence<AstElement>(null);
		for (int arg_index = argumentNames.length - 1; arg_index >= 0; arg_index--) {
			allNodes.insertNext(createAssignment(argumentNames, arg_index));
		}
		assert allNodes.get() == null;
		assert allNodes.size() == argumentNames.length;
		allNodes = allNodes.next();
		allNodes.last().insertNext(bodyNodes);
		assert allNodes.size() == argumentNames.length + bodyNodes.size();
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

}
