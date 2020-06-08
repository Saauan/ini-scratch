package ini;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.ast.AstElement;
import ini.ast.Sequence;

public class Utils {
	
	// TODO : Unfold loop and compilation final
	public static AstElement[] convertSequenceToArray(Sequence<AstElement> sequence) {
		final int nbElements = sequence.size();
		AstElement[] res = new AstElement[nbElements];
		for (int i = 0; i < nbElements; i++) {
			res[i] = sequence.get();
			sequence = sequence.next();
		}
		return res;
	}
	
	public static VirtualFrame findRootContext(VirtualFrame currentFrame) {
		while(!isRootContext(currentFrame)) {
			assert currentFrame.getArguments()[0] instanceof Frame : "The first argument of the frame was not a Frame";
			currentFrame = (VirtualFrame) currentFrame.getArguments()[0];
		}
		return currentFrame;
	}
	
	/**
	 * The frame is the root context if it has no frame calling it. Therefore its first argument is null
	 */
	public static boolean isRootContext(VirtualFrame frame) {
		return (frame.getArguments() == null ||
				frame.getArguments().length == 0 ||
				frame.getArguments()[0] == null);
	}
	
	/**
	 * The frame is the main frame if it has only one frame calling it, the root context
	 */
	public static boolean isMain(VirtualFrame frame) {
		return frame.getArguments().length > 0 &&
				frame.getArguments()[0] != null &&
				frame.getArguments()[0] instanceof VirtualFrame &&
				isRootContext((VirtualFrame) frame.getArguments()[0]);
	}
}
