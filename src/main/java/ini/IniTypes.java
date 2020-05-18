package ini;

import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem(value={Number.class, boolean.class,
	char.class, String.class})
public abstract class IniTypes {
}
