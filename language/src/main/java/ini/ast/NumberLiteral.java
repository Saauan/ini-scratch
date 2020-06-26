package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class NumberLiteral extends AstExpression implements Expression {

	public Number value;
	public int typeInfo;
	
	public NumberLiteral(Number value) {
		this.value=value;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(value);
	}
	
	public NumberLiteral applyTypeInfo() {
//		if (value == null || typeInfo == 0) {
//			return this;
//		}
//		switch (typeInfo) {
//		case TypeInfo.INTEGER:
//			value = ((Number) value).intValue();
//			break;
//		case TypeInfo.LONG:
//			value = ((Number) value).longValue();
//			break;
//		case TypeInfo.DOUBLE:
//			value = ((Number) value).doubleValue();
//			break;
//		case TypeInfo.FLOAT:
//			value = ((Number) value).floatValue();
//			break;
//		case TypeInfo.STRING:
//			break;
//		case TypeInfo.BOOLEAN:
//			break;
//		default:
//			Main.LOGGER.error("NO CONVERSION: " + typeInfo + " / " + value + " / " + value.getClass(), new Exception());
//		}
		return this;
	}
	
//	@Override
//	public void accept(Visitor visitor) {
//		visitor.visitNumberLiteral(this);
//	}

	@Override
	public Number executeGeneric(VirtualFrame virtualFrame) {
		return value;
	}
	
}
