package ini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;

import ini.runtime.IniExecutable;

/**
 * Manages the mapping from function names to {@link IniExecutable function
 * objects}
 * 
 * @author Tristan
 *
 */
public final class IniFunctionRegistry {
	private final IniLanguage language;
	private final Map<String, IniExecutable> functionMap;
//	private final IniExecutable functionsObject = new IniExecutable(); // TODO : implement if implementing interop

	public IniFunctionRegistry(IniLanguage language) {
		this.language = language;
		this.functionMap = new HashMap<String, IniExecutable>();
	}

	/**
	 * Returns the canonical {@link IniExecutable} object for the given name. If it
	 * does not exists, returns null.
	 */
	public IniExecutable lookup(String name) {
		IniExecutable result = functionMap.get(name);
		return result;
	}
	
	public IniExecutable register(String name, IniExecutable function) {
		this.functionMap.put(name, function);
		return function;
	}

	public List<IniExecutable> getFunctions() {
		List<IniExecutable> result = new ArrayList<>(this.functionMap.values());
		return result;
	}
}
