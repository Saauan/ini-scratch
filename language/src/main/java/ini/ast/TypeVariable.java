package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.type.AstAttrib;
import ini.type.Type;

public class TypeVariable extends Variable {

	public TypeVariable component;
	public TypeVariable superType;
	public List<TypeVariable> context;
	public List<TypeVariable> typeParameters;
	public Collection<String> fields;
	public boolean parameter = false;

	public static TypeVariable create(String name, Collection<String> fields, TypeVariable... dependentTypes) {
		TypeVariable t = new TypeVariable(name);
		t.fields = fields;
		t.typeParameters = new ArrayList<>(Arrays.asList(dependentTypes));
		return t;
	}
	
	public TypeVariable(String name) {
		super(name);
	}

	public TypeVariable(String name, boolean parameter) {
		super(name);
		this.parameter = parameter;
	}

	public TypeVariable(TypeVariable component) {
		super(null);
		this.component = component;
	}

	public boolean isList() {
		return component != null;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (isList()) {
			component.prettyPrint(out);
			out.append("*");
		} else {
			super.prettyPrint(out);
		}
	}

	public Type lookupTypeVariable(String name) {
		if (context == null || name == null) {
			return null;
		}
		for (TypeVariable v : context) {
			if (name.equals(v.name)) {
				return v.getType();
			}
		}
		return null;
	}

	public Type getType() {
//		if (type != null) {
//			return type;
//		}
//		if (parameter) {
//			type = parser.types.createType();
//			if (superType != null) {
//				type.superType = parser.types.getSimpleType(superType.name);
//			}
//		} else {
//			if (isList()) {
//				type = parser.types.getListOf(component.getType());
//			} else {
//				Type t = lookupTypeVariable(name);
//				if (t == null) {
//					if (typeParameters == null || typeParameters.isEmpty()) {
//						type = parser.types.getSimpleType(name);
//					} else {
//						type = parser.types.createType(name);
//						type.variable = false;
//					}
//					if (this.superType != null) {
//						type.superType = this.superType.getType();
//					}
//				} else {
//					type = t;
//				}
//			}
//			if (typeParameters != null) {
//				for (TypeVariable v : typeParameters) {
//					type.addTypeParameter(v.getType());
//				}
//			}
//		}
//		return type;
		return null;
	}

	public boolean isTypeRegistered(AstAttrib attrib) {
//		if (component != null) {
//			return component.isTypeRegistered(attrib);
//		} else {
//			return attrib.parser.types.isRegistered(name);
//		}
		return false;
	}

	public boolean isParameter() {
		return parameter;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitTypeVariable(this);;
	}

	@Override
	public Object executeGeneric(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
}
