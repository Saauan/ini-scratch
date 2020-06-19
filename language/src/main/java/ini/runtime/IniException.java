package ini.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

public class IniException extends RuntimeException implements TruffleException {

	private static final long serialVersionUID = -6799734410727348507L;

	private final Node location;

	@TruffleBoundary
	public IniException(String message, Node location) {
		super(message);
		this.location = location;
	}

	@Override
	public final Throwable fillInStackTrace() {
		return this;
	}

	public Node getLocation() {
		return location;
	}

	/**
	 * Provides a user-readable message for run-time type errors. SL is strongly
	 * typed, i.e., there are no automatic type conversions of values.
	 */
	@TruffleBoundary
	public static IniException typeError(Node operation, Object... values) {
		StringBuilder result = new StringBuilder();
		result.append("Type error : ");
		result.append(String.format("The operation %s is not compatible with the values ", operation.getClass()));
		for (int i = 0; i < values.length; i++) {
			result.append(String.format("%s:%s", values[i], values[i].getClass()));
		}
		return new IniException(result.toString(), operation);
	}
}
