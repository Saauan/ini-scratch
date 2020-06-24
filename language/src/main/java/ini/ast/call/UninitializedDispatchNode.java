package ini.ast.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import ini.ast.Invocation;

final public class UninitializedDispatchNode extends DispatchNode {
    @Override
    public Object executeDispatch(VirtualFrame virtualFrame,
            CallTarget callTarget, Object[] arguments) {
        
        Node cur = this;
        int size = 0;
        while (cur.getParent() instanceof DispatchNode) {
            cur = cur.getParent();
            size++;
        }
        Invocation invokeNode = (Invocation) cur.getParent();

        DispatchNode replacement;
        CompilerDirectives.transferToInterpreterAndInvalidate();
        if (size < INLINE_CACHE_SIZE) {
            // There's still room in the cache. Add a new DirectDispatchNode.
            DispatchNode next = new UninitializedDispatchNode();
            replacement = new DirectDispatchNode(next, callTarget);
            this.replace(replacement);
        } else {
            replacement = new GenericDispatchNode();
            invokeNode.dispatchNode.replace(replacement);
        }

        // Call function with newly created dispatch node.
        return replacement.executeDispatch(virtualFrame, callTarget, arguments);
    }
}
