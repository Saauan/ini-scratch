package ini;

import com.oracle.truffle.api.dsl.TypeSystem;

import ini.ast.BooleanLiteral;
import ini.ast.CharLiteral;
import ini.ast.NumberLiteral;
import ini.ast.StringLiteral;

@TypeSystem({NumberLiteral.class, BooleanLiteral.class, CharLiteral.class, StringLiteral.class})
public abstract class IniTypes {

	public IniTypes() {
		// TODO Auto-generated constructor stub
	}

	
	// TODO : Add conversion methods between types
}
