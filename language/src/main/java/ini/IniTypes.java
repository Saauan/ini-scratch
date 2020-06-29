package ini;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

/**
 * This class defines all the types used in ini.
 * 
 * Note : There can also be custom classes
 *
 */
@TypeSystem(value={byte.class, int.class, long.class, float.class, double.class, boolean.class,
	char.class, String.class})
public abstract class IniTypes {
	
    /**
     * Informs the Truffle DSL that a primitive {@code int} value can be used in all
     * specializations where a {@code long} is expected.
     */
    @ImplicitCast
    public static long castLong(int value) {
        return (long) value;
    }
}
