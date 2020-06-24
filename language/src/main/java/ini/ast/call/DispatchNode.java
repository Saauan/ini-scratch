package ini.ast.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class DispatchNode extends Node {
    protected static final int INLINE_CACHE_SIZE = 3;

    public abstract Object executeDispatch(VirtualFrame virtualFrame,
            CallTarget callTarget, Object[] argumentValues);
}
