package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;

import ini.parser.IniParser;

public class Constructor extends NamedElement {

	private static int index = 1;

	public UserType userType;
	public List<Field> fields = new ArrayList<Field>();
	public Map<String, Field> fieldMap = new HashMap<String, Field>();

	public Constructor(String name,
			List<Field> fields) {
		super(name);

		if (this.name == null) {
			this.name = "_C" + index++;
		}
		this.fields = fields;
		if (fields != null) {
			for (Field f : fields) {
				this.fieldMap.put(f.name, f);
			}
		}
	}

	public Constructor element;

	public Constructor(Constructor element) {
		super(null);
		this.element = element;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (element != null) {
			element.prettyPrint(out);
			out.print("*");
		} else {
			if (name != null) {
				out.print(name);
			}
			if (fields != null && !fields.isEmpty()) {
				out.print("[");
				prettyPrintList(out, fields, ",");
				out.print("]");
			}
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitConstructor(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}
	
}
