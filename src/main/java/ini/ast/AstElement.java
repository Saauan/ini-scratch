package ini.ast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import ini.IniTypes;
import ini.parser.IniParser;
import ini.type.Type;
import ini.IniTypesGen;

@TypeSystemReference(IniTypes.class)
@NodeInfo(language = "INI", description = "The abstract base node for all expressions")
public abstract class AstElement extends Node implements AstNode {

	transient public IniParser parser;
	transient public Token token;
	public int nodeTypeId = -1;
	transient public Type type;
	public String owner;
	public List<Expression> annotations;

	/**
	 * The execute method when no specialization is possible. This is the most
	 * general case, therefore it must be provided by all subclasses.
	 */
	@Override
	public abstract Object executeGeneric(VirtualFrame virtualFrame);

	/**
	 * When we use an expression at places where a {@link SLStatementNode statement}
	 * is already sufficient, the return value is just discarded.
	 */
	@Override
	public void executeVoid(VirtualFrame frame) {
		executeGeneric(frame);
	}

	/*
	 * Execute methods for specialized types. They all follow the same pattern: they
	 * call the generic execution method and then expect a result of their return
	 * type. Type-specialized subclasses overwrite the appropriate methods.
	 */

	@Override
	public byte executeByte(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectByte(this.executeGeneric(virtualFrame));
	}

	@Override
	public int executeInteger(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectInteger(this.executeGeneric(virtualFrame));
	}

	@Override
	public long executeLong(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectLong(this.executeGeneric(virtualFrame));
	}

	@Override
	public float executeFloat(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectFloat(this.executeGeneric(virtualFrame));
	}

	@Override
	public double executeDouble(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectDouble(this.executeGeneric(virtualFrame));
	}

	@Override
	public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectBoolean(this.executeGeneric(virtualFrame));
	}

	@Override
	public char executeChar(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectCharacter(this.executeGeneric(virtualFrame));
	}

	@Override
	public String executeString(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return IniTypesGen.expectString(this.executeGeneric(virtualFrame));
	}

	/**
	 * Returns the function identifier used as a key for the FrameSlots.
	 */
	public static String getFunctionIdentifier(String functionName, int nbParameters) {
		return String.format("%s parameters:%d", functionName, nbParameters);
	}

	@Override
	public String getAnnotationStringValue(String... keys) {
		if (annotations != null && !annotations.isEmpty()) {
			for (Expression e : annotations) {
				if (e instanceof Assignment) {
					Assignment a = (Assignment) e;
					String name = a.assignee.toString();
					if (ArrayUtils.contains(keys, name)) {
						if (a.assignment instanceof StringLiteral) {
							return ((StringLiteral) a.assignment).value;
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
						if (a.assignment instanceof NumberLiteral) {
							return ((NumberLiteral) a.assignment).value;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public <T extends AstNode> T getAnnotationNode(String... keys) {
		if (annotations != null && !annotations.isEmpty()) {
			for (Expression e : annotations) {
				if (e instanceof Assignment) {
					Assignment a = (Assignment) e;
					String name = a.assignee.toString();
					if (ArrayUtils.contains(keys, name)) {
						@SuppressWarnings("unchecked")
						T assignment = (T) a.assignment;
						return assignment;
					}
				}
			}
		}
		return null;
	}

	@Override
	public int nodeTypeId() {
		return nodeTypeId;
	}

	public AstElement(IniParser parser, Token token) {
		super();
		// if(token==null) {
		// throw new RuntimeException("token cannot be null");
		// }
		this.parser = parser;
		this.token = token;
		if (parser != null) {
			this.owner = parser.env.node;
		}
	}

	public AstElement() {
		super();
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
	public Token token() {
		return token;
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
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

}
