package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import ini.type.AstAttrib;
import ini.type.AttrContext;
public abstract class Executable extends AstExpression implements Expression {

	/**
	 * Converts a list of Parameters to an array of frame slots. The identifier of
	 * the slot is the parameter name
	 */
	@ExplodeLoop
	public static FrameSlot[] convertListOfParametersToArrayOfFrameSlot(List<Parameter> parameters, FrameDescriptor frameDescriptor) {
		FrameSlot[] result = new FrameSlot[parameters.size()];
		final int nbParam = parameters.size();
		CompilerAsserts.partialEvaluationConstant(nbParam);
		for (int i = 0; i < nbParam; i++) {
			result[i] = frameDescriptor.addFrameSlot(parameters.get(i).name);
		}
		return result;
	}

	public List<Parameter> parameters;

//	public transient Context accessibleContext;
	public transient AttrContext accessibleAttrContext;
	public List<Executable> overloads;
	public String name;
	
	public Executable(String name, List<Parameter> parameters) {
		super();
		this.name = name;
		this.parameters = parameters;
	}
	
	public void addOverload(Executable executable) {
		if (overloads == null) {
			overloads = new ArrayList<Executable>();
		}
		overloads.add(executable);
	}

	public int getMandatoryParameterCount() {
		int i = 0;
		for (Parameter p : parameters) {
			if (p.defaultValue == null) {
				i++;
			}
		}
		return i;
	}

	public boolean match(AstAttrib attrib, Invocation invocation) {
		return invocation.argumentNodes.length >= getMandatoryParameterCount()
				&& invocation.argumentNodes.length <= parameters.size();
	}

	public boolean isAmbiguousWith(AstAttrib attrib, Executable executable) {
		boolean smaller = this.getMandatoryParameterCount() <= executable.getMandatoryParameterCount();
		Executable e1 = smaller ? this : executable;
		Executable e2 = smaller ? executable : this;
		return e1.parameters.size() >= e2.getMandatoryParameterCount();
	}

	public List<Executable> findAmbiguousOverloads(AstAttrib attrib, Executable executable) {
		List<Executable> result = new ArrayList<>();
		if (isAmbiguousWith(attrib, executable)) {
			result.add(this);
		}
		if (overloads != null) {
			for (Executable e : overloads) {
				if (e.isAmbiguousWith(attrib, executable)) {
					result.add(e);
				}
			}
		}
		return result;
	}

	public List<Executable> findMatchingOverloads(AstAttrib attrib, Invocation invocation) {
		List<Executable> executables = new ArrayList<>();
		if (this.match(attrib, invocation)) {
			executables.add(this);
		} else if (overloads != null) {
			for (Executable e : overloads) {
				if (e.match(attrib, invocation)) {
					executables.add(e);
				}
			}
		}
		return executables;
	}

	public Executable resolveOverload(AstAttrib attrib, Invocation invocation) {
		if (this.match(attrib, invocation)) {
			return this;
		} else if (overloads != null) {
			for (Executable e : overloads) {
				if (e.match(attrib, invocation)) {
					return e;
				}
			}
		}
		throw new RuntimeException("cannot find matching overload for invocation " + invocation);
	}

	@Deprecated
	protected final void setDefaultValue(int parameterIndex, AstExpression expression) {
		parameters.get(parameterIndex).defaultValue = expression;
	}


//	protected final Type getParameterType(int index) {
//		return getType().getTypeParameters().get(index);
//	}
//
//	protected final Type getReturnType() {
//		return getType().getReturnType();
//	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + "(");
		prettyPrintList(out, parameters, ",");
		out.print(")");
	}

	@Override
	public String toString() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream out = new PrintStream(baos);) {
			out.print((name == null ? "<lambda>" : name) + "(");
			out.print(StringUtils.join(parameters, ","));
			out.print(")");
			return baos.toString("UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	public final Type getType() {
//		if (this.type == null) {
//			this.type = parser.types.createFunctionalType(parser.types.createType());
//			for (int i = 0; i < parameters.size(); i++) {
//				this.type.addTypeParameter(new Type(parser.types));
//			}
//		}
//		return this.type;
//	}

//	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
//		Type functionalType = new Type(parser.types, "function");
//		if (name != null && name.equals("main")) {
//			if (parameters != null && parameters.size() == 1) {
//				functionalType
//						.addTypeParameter(parser.types.getDependentType("Map", parser.types.INT, parser.types.STRING));
//			}
//			functionalType.setReturnType(parser.types.VOID);
//		} else {
//			functionalType.setReturnType(new Type(parser.types));
//		}
//		for (int i = 0; i < parameters.size(); i++) {
//			functionalType.addTypeParameter(new Type(parser.types));
//		}
//		return functionalType;
//	}

}
