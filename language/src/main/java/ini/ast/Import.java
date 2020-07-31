package ini.ast;

import java.io.PrintStream;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniContext;
import ini.IniLanguage;
import ini.parser.IniParser;
import ini.runtime.IniException;
import ini.runtime.IniFunction;

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
		System.out.println("Importing... in");
		IniContext context = this.lookupContextReference(IniLanguage.class).get();
		// if imported files in context contains this file path, do not import
		if (context.isFileImported(filePath)){
			throw new IniException(String.format("The file %s is already imported", filePath), this);
		}
		IniParser localParser;
		try {
			localParser= IniParser.createParserForFile(null, null, this.filePath.toString());
			// Create parser
			localParser.parse();
			if (localParser.hasErrors()) {
				localParser.printErrors(System.err);
				throw new IniException("Error while importing file '" + this.filePath, this);
			} else {
				context.addImportedFile(filePath);
			}
		} catch (java.io.FileNotFoundException e) {
			throw new IniException(String.format("Error : Cannot import file %s : File not Found", filePath), this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		IniFunction function = IniFunction.createStatic(
        		null,
        		"main_import",
        		new FrameSlot[] {},
        		localParser.topLevels.toArray(new AstElement[0]),
        		context.getGlobalFrame().getFrameDescriptor());
		System.out.println("Importing...");
		function.callTarget.call();
	}
}
