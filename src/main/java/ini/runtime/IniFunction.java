package ini.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.MaterializedFrame;

import ini.IniLanguage;
import ini.ast.AstElement;
import ini.ast.IniRootNode;
import ini.ast.Sequence;

public class IniFunction {
    public final RootCallTarget callTarget;
    private MaterializedFrame lexicalScope;

    public IniFunction(RootCallTarget callTarget) {
        this.callTarget = callTarget;
    }

    public MaterializedFrame getLexicalScope() {
        return lexicalScope;
    }

    public void setLexicalScope(MaterializedFrame lexicalScope) {
        this.lexicalScope = lexicalScope;
    }

    public static IniFunction create(IniLanguage lang, FrameSlot[] arguments,
    		Sequence<AstElement> bodyNodes, FrameDescriptor frameDescriptor) {
        return new IniFunction(
                Truffle.getRuntime().createCallTarget(
                        IniRootNode.create(lang, arguments, bodyNodes, frameDescriptor)));
    }
    
    // e.g. MumblerFunction.create(this, new FrameSlot[] {}, nodes, context.getGlobalFrame().getFrameDescriptor())
}
