package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public interface AstNode {

	public void executeVoid(VirtualFrame frame);
	
	void accept(Visitor visitor);
	
	void prettyPrint(PrintStream out);
	
	public String getAnnotationStringValue(String... keys);
	public Number getAnnotationNumberValue(String... keys);
	public <T extends AstNode> T getAnnotationNode(String... keys);
}

