package ini.ast;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;

import ini.IniLanguage;

public class IniRootNode extends RootNode {
	
	/** The name of the function, for printing purposes only. */
    private final String name;

	@Children
	private final AstElement[] bodyNodes;

	public IniRootNode(IniLanguage language, String name, AstElement[] bodyNodes, FrameDescriptor frameDescriptor) {
		super(language, frameDescriptor);
		this.name = name;
		this.bodyNodes = bodyNodes;
	}

	@Override
	@ExplodeLoop
	public Object execute(VirtualFrame frame) {
		AstElement[] s = this.bodyNodes;
		int nbNodes = s.length;
		CompilerAsserts.compilationConstant(nbNodes);
		int i;
		try {
			for (i = 0; i < nbNodes-1; i++) {
				s[i].executeGeneric(frame);
			}
			return s[i].executeGeneric(frame);
		} catch(ReturnException e) {
			return e.getResult();
		}
	}

	public static IniRootNode create(IniLanguage lang, String name, FrameSlot[] parametersSlots, AstElement[] bodyNodes,
			FrameDescriptor frameDescriptor) {
		AstElement[] allNodes = new AstElement[parametersSlots.length+bodyNodes.length];
		// Insert all parameters
		if(parametersSlots.length>0) {
			for (int arg_index = 0; arg_index < parametersSlots.length; arg_index++) {
				allNodes[arg_index] = createAssignment(parametersSlots, arg_index);
			}
		}
		System.arraycopy(bodyNodes, 0, allNodes,
                parametersSlots.length, bodyNodes.length);
		return new IniRootNode(lang, name, allNodes, frameDescriptor);
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

	@Override
	public String getName() {
		return name;
	}

    @Override
    public String toString() {
        return "root " + name;
    }

}
