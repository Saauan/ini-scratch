package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.VirtualFrame;

public class Import extends AstElement implements Comparable<Import> {

	public String filePath;
	public IniParser importParser;
	
	public Import(IniParser parser, Token token, String filePath) {
		super(parser, token);
		this.filePath=filePath;
		this.nodeTypeId=IMPORT;
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
	public Object execute(VirtualFrame virtualFrame) {
		// TODO Auto-generated method stub
		return null;
	}
}
