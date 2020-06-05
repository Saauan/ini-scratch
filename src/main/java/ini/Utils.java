package ini;

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
}
