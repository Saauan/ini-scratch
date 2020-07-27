package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.runtime.IniList;

@NodeInfo(shortName = "size")
@GenerateNodeFactory
public abstract class SizeFunction extends BuiltInExecutable {

	public SizeFunction() {
	}
	
	@Specialization
	public int size(IniList list) {
		return list.getSize();
	}

}
