package ini.ast.expression;

import ini.runtime.IniException;

public class Substraction implements Operation {
	
	private final static Substraction INSTANCE = new Substraction();
	
	private Substraction(){}

	public static Substraction getInstance() {
		return INSTANCE;
	}
	
	@Override
	public int doOp(int value) {
		return value - 1;
	}

	@Override
	public float doOp(float value) {
		return value - 1;
	}

	@Override
	public double doOp(double value) {
		return value - 1;
	}

	@Override
	public long doOp(long value) {
		return value - 1;
	}

	@Override
	public byte doOp(byte value) {
		throw new IniException(String.format("value %s is not compatible with substraction", value), null);
	}

	@Override
	public boolean doOp(boolean value) {
		throw new IniException(String.format("value %s is not compatible with substraction", value), null);
	}

	@Override
	public Object doOp(Object value) {
		throw new IniException(String.format("value %s is not compatible with substraction", value), null);
	}
	
	public String toString() {
		return "-";
	}

}
