package ini.ast.expression;

public interface Operation {

	public int doOp(int value);
	public float doOp(float value);
	public double doOp(double value);
	public long doOp(long value);
	public byte doOp(byte value);
	public boolean doOp(boolean value);
	public Object doOp(Object value);
}
