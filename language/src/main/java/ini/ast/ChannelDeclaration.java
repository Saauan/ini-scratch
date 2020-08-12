package ini.ast;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniLanguage;
import ini.broker.ChannelConfiguration;
import ini.runtime.IniException;

/**
 * The node represents a declaration of a channel
 * 
 * Looks like this "declare channel c()" with "c" being the name of the channel 
 *
 */
@NodeInfo(shortName="declare channel")
public class ChannelDeclaration extends NamedElement {

	private static long localId = 1;

	public static long getLocalId() {
		return localId++;
	}

	public enum Visibility {
		LOCAL, APPLICATION, GLOBAL
	}

	public static final String STOP_MESSAGE = "__STOP__";
	public static final String VOID_MESSAGE = "__VOID__";

	public boolean indexed = false;
	public Visibility visibility;
	public String mappedName;
	public Integer size;
	private transient Map<Integer, ChannelDeclaration> components;

	public ChannelDeclaration(String name,
			Visibility visibility, boolean indexed, List<Expression> annotations) {
		super(name);
		if (name == null) {
			throw new RuntimeException("channel name cannot be null");
		}
		this.visibility = visibility == null ? Visibility.LOCAL : visibility;
		this.indexed = indexed;
		this.annotations = annotations;
		this.size = (Integer) getAnnotationNumberValue("capacity", "size");
		this.mappedName = getAnnotationStringValue("name");
		if (this.mappedName == null) {
			this.mappedName = name;
		}
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("channel " + name + "(");
		out.print(")");
		if (annotations != null) {
			out.print(" " + annotations);
		}
	}

	public ChannelDeclaration getComponent(int i) {
		if (!indexed) {
			throw new RuntimeException("cannot access component on non-indexed channel");
		}
		if (components == null) {
			components = new HashMap<Integer, ChannelDeclaration>();
		}
		if (!components.containsKey(i)) {
			components.put(i, new ChannelDeclaration(mappedName + i, visibility, false,
					annotations));
		}
		return components.get(i);
	}

	public ChannelConfiguration getChannelConfiguration() {
		if (indexed) {
			throw new RuntimeException("illegal operation on indexed channels");
		}
		if (size != null) {
			return new ChannelConfiguration(size);
		} else {
			return null;
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitChannel(this);
	}

	/**
	 * Stores the channel in the frame. The identifier is the name of the channel
	 * 
	 * The channel is also added to the globalVariables
	 */
	@Override
	public void executeVoid(VirtualFrame frame) {
		FrameSlot slot = frame.getFrameDescriptor().findOrAddFrameSlot(name);
		FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(slot);
		if(!(kind == FrameSlotKind.Illegal || kind == FrameSlotKind.Object)) {
			throw new IniException(String.format("The Variable %s already exists and is not of type ChannelDeclaration", name), this);
		}
		else {
			frame.setObject(slot, this);
			frame.getFrameDescriptor().setFrameSlotKind(slot, FrameSlotKind.Object);
			lookupContextReference(IniLanguage.class).get().addGlobalVariable(name, this);
		}
	}

}
