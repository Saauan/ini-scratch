package ini.eval.function;

import java.util.ArrayList;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.Executable;
import ini.ast.IniRootNode;
import ini.ast.Parameter;
import ini.ast.ReadArgumentFromContextNode;
import ini.ast.Visitor;
import ini.parser.IniParser;
import ini.runtime.IniFunction;

@GenerateNodeFactory()
public abstract class BuiltInExecutable extends Executable {
	
	public static String defaultName;

	public BuiltInExecutable(String... parameterNames) {
		super(null, null, defaultName, null);
		parameters = new ArrayList<Parameter>();
		for (String parameterName : parameterNames) {
			parameters.add(new Parameter(null, null, parameterName));
		}
	}
	
    public static IniFunction createBuiltinFunction(
            IniLanguage lang,
            NodeFactory<? extends BuiltInExecutable> factory,
            VirtualFrame outerFrame) {
        int argumentCount = factory.getExecutionSignature().size();
        AstElement[] argumentNodes = new AstElement[argumentCount];
        for (int i=0; i<argumentCount; i++) {
            argumentNodes[i] = new ReadArgumentFromContextNode(null, null, i);
        }
        String[] argumentNames =  {""};
        BuiltInExecutable node = factory.createNode(argumentNames, (Object) argumentNodes);
        return new IniFunction(Truffle.getRuntime().createCallTarget(
                new IniRootNode(lang, new AstElement[] {node},
                        new FrameDescriptor())));
    }
	
	@Override
	public void accept(Visitor visitor) {
		// ignore
	}
	
}
