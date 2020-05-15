package ini;

import com.oracle.truffle.api.dsl.TypeSystem;

import ini.ast.BooleanLiteral;
import ini.ast.CharLiteral;
import ini.ast.NumberLiteral;
import ini.ast.StringLiteral;

@TypeSystem(value={NumberLiteral.class, BooleanLiteral.class,
	CharLiteral.class, StringLiteral.class})
public abstract class IniTypes {
}
