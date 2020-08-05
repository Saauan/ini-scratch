package ini.ast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.IniTypes;

@TypeSystemReference(IniTypes.class)
@NodeInfo(language = "INI", description = "The abstract base node for all expressions")
@GenerateWrapper
public abstract class AstElement extends Node implements AstNode, InstrumentableNode {

	public List<Expression> annotations;

	public AstElement() {
		super();
	}

	/**
	 * Default behaviour for a node
	 */
	@Override
	public abstract void executeVoid(VirtualFrame frame);

	/**
	 * Returns the function identifier used as a key for the FrameSlots.
	 */
	public static String getExecutableIdentifier(String functionName, int nbParameters) {
		return String.format("%s parameters:%d", functionName, nbParameters);
	}

	static public void prettyPrintList(PrintStream out, List<? extends AstNode> nodes, String separator) {
		if (nodes == null) {
			return;
		}
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).prettyPrint(out);
			if (i < nodes.size() - 1) {
				out.print(separator);
			}
		}
	}

	@Override
	public void accept(Visitor visitor) {
		System.out.println("DEBUG : No visitor accepted, default method in AstElement"); // TODO
	}

	@Override
	public String toString() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		prettyPrint(new PrintStream(os));
		String s = os.toString();
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	

	@Override
	public String getAnnotationStringValue(String... keys) {
		if (annotations != null && !annotations.isEmpty()) {
			for (Expression e : annotations) {
				if (e instanceof Assignment) {
					AssignmentNodeGen a = (AssignmentNodeGen) e;
					String name = a.assignee.toString();
					if (ArrayUtils.contains(keys, name)) {
						AstExpression assignment = null;
						try {
							assignment = (AstExpression) FieldUtils.readField(a, "assignmentValue_", true);
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						}
						if (assignment instanceof StringLiteral) {
							return ((StringLiteral) assignment).value;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Number getAnnotationNumberValue(String... keys) {
		if (annotations != null && !annotations.isEmpty()) {
			for (Expression e : annotations) {
				if (e instanceof Assignment) {
					Assignment a = (Assignment) e;
					String name = a.assignee.toString();
					if (ArrayUtils.contains(keys, name)) {
						AstExpression assignment = null;
						try {
							assignment = (AstExpression) FieldUtils.readField(a, "assignmentValue_", true);
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						}
						if (assignment instanceof NumberLiteral) {
							return ((NumberLiteral) assignment).value;
						}
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AstNode> T getAnnotationNode(String... keys) {
		if (annotations != null && !annotations.isEmpty()) {
			for (Expression e : annotations) {
				if (e instanceof Assignment) {
					Assignment a = (Assignment) e;
					String name = a.assignee.toString();
					if (ArrayUtils.contains(keys, name)) {
						T assignment;
						try {
							assignment = (T) FieldUtils.readField(a, "assignmentValue_", true);
							return assignment;
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean isInstrumentable() {
		return true;
	}
	
	@Override
	public WrapperNode createWrapper(ProbeNode probe) {
		return new AstElementWrapper(this, probe);
	}

}
