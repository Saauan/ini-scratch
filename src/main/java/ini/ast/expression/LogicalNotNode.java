package ini.ast.expression;

import java.io.PrintStream;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.AstElement;
import ini.ast.Token;
import ini.parser.IniParser;
import ini.runtime.IniException;

@NodeChild("valueNode")
@NodeInfo(shortName = "!")
public abstract class LogicalNotNode extends AstElement {

	public LogicalNotNode(IniParser parser, Token token) {
		super(parser, token);
	}
	
	@Specialization
    protected boolean equal(boolean value) {
        return !value;
    }

    @Fallback
    protected Object typeError(Object value) {
        throw IniException.typeError(this, value);
    }
    
	@Override
	public void prettyPrint(PrintStream out) {
		out.print("! [SomeCondition]");
	}

}
