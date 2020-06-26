package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Import extends AstElement implements Comparable<Import> {

	public String filePath;
	public IniParser importParser;
	
	public Import(String filePath) {
		super();
		this.filePath=filePath;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("import \""+filePath+"\"");
	}

	@Override
	public int compareTo(Import o) {
		return filePath.compareTo(o.filePath);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitImport(this);
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		// TODO Auto-generated method stub
		
	}
}
