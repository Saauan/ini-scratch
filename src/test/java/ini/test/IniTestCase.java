package ini.test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ini.Main;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public abstract class IniTestCase extends TestCase {

	public static IniParser currentParser;
	
	static final Logger logger = LoggerFactory.getLogger("test");

	protected static boolean skipTestsUsingBroker = false;
	
	public IniTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Main.LOGGER.info("=====================================================");
		//outputStream = new ByteArrayOutputStream();
		//out = new PrintStream(outputStream);
		//System.setOut(out);
	}

	protected void parseAndAttribCode(String code, Consumer<IniParser> parsingAssertions,
			Consumer<AstAttrib> attribAssertions) {
		IniParser parser = null;
		try {
			parser = IniParser.createParserForCode(null, null, code);
			parser.parse();
			parsingAssertions.accept(parser);
			AstAttrib attrib = new AstAttrib(parser);
			attrib.attrib(parser);
			attrib.unify();
			attribAssertions.accept(attrib);
		} catch (Exception e) {
			if (parser != null && parser.hasErrors()) {
				parser.printErrors(System.err);
			}
			e.printStackTrace();
			fail();
		}
	}

	protected void parseAndAttribFile(String file, Consumer<IniParser> parsingAssertions,
			Consumer<AstAttrib> attribAssertions) {
		IniParser parser = null;
		try {
			parser = IniParser.createParserForFile(null, null, file);
			parser.parse();
			parsingAssertions.accept(parser);
			AstAttrib attrib = new AstAttrib(parser);
			attrib.attrib(parser);
			attrib.unify();
			attribAssertions.accept(attrib);
		} catch (Exception e) {
			if (parser != null && parser.hasErrors()) {
				parser.printErrors(System.err);
			}
			e.printStackTrace();
			fail();
		}
	}

	protected void testFile(String file, BiConsumer<IniParser, String> assertions) {
		testFile(file, 0, null, new ByteArrayOutputStream(), assertions);
	}

	protected void testFile(String file, String node, BiConsumer<IniParser, String> assertions) {
		testFile(file, 0, node, new ByteArrayOutputStream(), assertions);
	}

	protected void testFile(String file, long sleepTime, BiConsumer<IniParser, String> assertions) {
		testFile(file, sleepTime, null, new ByteArrayOutputStream(), assertions);
	}

	protected void testFile(String file, OutputStream outputStream, BiConsumer<IniParser, String> assertions) {
		testFile(file, 0, null, outputStream, assertions);
	}

	protected void testFile(String file, String node, OutputStream outputStream, BiConsumer<IniParser, String> assertions) {
		testFile(file, 0, node, outputStream, assertions);
	}

	protected void testFile(String file, long sleepTime, OutputStream outputStream, BiConsumer<IniParser, String> assertions) {
		testFile(file, sleepTime, null, outputStream, assertions);
	}

	protected void testFile(String file, long sleepTime, String node, BiConsumer<IniParser, String> assertions) {
		testFile(file, sleepTime, node, new ByteArrayOutputStream(), assertions);
	}
	
	protected void testFile(String file, long sleepTime, String node, OutputStream outputStream, BiConsumer<IniParser, String> assertions) {
		IniParser parser = null;
		try {
			parser = file == null ? IniParser.createParserForCode(null, null, "process main() {}")
					: IniParser.createParserForFile(null, null, file);
			parser.out = new PrintStream(outputStream);
			currentParser = parser;
			//System.setOut(out);

			if (node != null) {
				parser.env.deamon = true;
				parser.env.node = node;
			}
			parser.parse();
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
			AstAttrib attrib = new AstAttrib(parser);
			attrib.attrib(parser);
			attrib.unify();
			assertEquals("expected 0 errors: " + attrib.errors, 0, attrib.errors.size());
			Main.mainEval(parser, attrib, null);
			if (sleepTime > 0) {
				Thread.sleep(sleepTime);
			}
			if (assertions != null) {
				assertions.accept(parser, outputStream.toString());
			}
		} catch (Exception e) {
			if (parser != null && parser.hasErrors()) {
				parser.printErrors(System.err);
			}
			e.printStackTrace();
			fail();
		}
	}

}
