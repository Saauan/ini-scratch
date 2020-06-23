package ini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;

import ini.runtime.IniFunction;

/**
 * Manages the mapping from function names to {@link IniFunction function
 * objects}
 * 
 * @author Tristan
 *
 */
public final class IniFunctionRegistry {
	private final IniLanguage language;
	private final Map<String, IniFunction> functionMap;
//	private final IniFunction functionsObject = new IniFunction(); // TODO : implement if implementing interop

	public IniFunctionRegistry(IniLanguage language) {
		this.language = language;
		this.functionMap = new HashMap<String, IniFunction>();
	}

	/**
	 * Returns the canonical {@link IniFunction} object for the given name. If it
	 * does not exists, returns null.
	 */
	public IniFunction lookup(String name) {
		IniFunction result = functionMap.get(name);
		return result;
	}
	
	public IniFunction register(String name, IniFunction function) {
		this.functionMap.put(name, function);
		return function;
	}

	public List<IniFunction> getFunctions() {
		List<IniFunction> result = new ArrayList<>(this.functionMap.values());
		return result;
	}
}
