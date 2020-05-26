package ini;

import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem(value={int.class, long.class, float.class, double.class, boolean.class,
	char.class, String.class})
public abstract class IniTypes {
}
