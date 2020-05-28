package ini.test.eval;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import ini.test.IniTestCase;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}

	public void testAlgebraicExpressions() {
		testFile("ini/truffle/testPrint.ini",
				(p, out) -> assertEquals("hello worldhello world", out));
	}
}
