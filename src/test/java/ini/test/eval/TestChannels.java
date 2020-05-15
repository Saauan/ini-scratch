package ini.test.eval;

import ini.broker.LocalBrokerClient;
import ini.test.IniTestCase;

public class TestChannels extends IniTestCase {

	public TestChannels(String name) {
		super(name);
	}

	public void testProdCons() {
		testFile("ini/test/channels/prod_cons.ini", (p, out) -> assertEquals("end prod\nend cons\n", out));
	}

	public void testChannel1() {
		testFile("ini/test/channels/channel1.ini", (p, out) -> assertEquals("string value = coucou 1\n", out));
	}

	public void testChannel2() {
		testFile("ini/test/channels/channel2.ini", (p, out) -> assertEquals("int value = 1\n", out));
	}

	public void testChannel3() {
		testFile("ini/test/channels/channel3.ini",
				(p, out) -> assertEquals(
						"{firstNames:[\"Renaud\",\"Bruno\",\"Pierre\"],height:184,lastName:\"Pawlak\"}\nperson value = Person[firstNames=[Renaud,Bruno,Pierre],height=184,lastName=Pawlak]\n{firstNames:[\"Renaud\",\"Bruno\",\"Pierre\"],height:184,lastName:\"Pawlak\"}\n",
						out));
	}

	public void testChannel4() {
		testFile("ini/test/channels/channel4.ini",
				(p, out) -> assertEquals("list value = [\"Renaud\",\"Bruno\",\"Pierre\"]\nBruno\n", out));
	}

	public void testChannel5() {
		testFile("ini/test/channels/channel5.ini", (p, out) -> assertEquals("dict value = {key:\"abc\"}\nabc\n", out));
	}

	public void testChannel6() {
		testFile("ini/test/channels/channel6.ini", (p, out) -> assertEquals("double value = 1.0\n", out));
	}

	public void testProcessCommunication() {
		testFile("ini/test/channels/process_communication.ini", (p, out) -> assertEquals(
				"processes started\nchannel c1(Int): 1\nchannel c2(Int): 2\nend of pipeline: 3\n", out));
	}

	public void testFacDistributed() {
		testFile("ini/test/channels/fac_distributed.ini", (p, out) -> assertEquals(
				"p(1) started\np(2) started\np(3) started\np(4) started\np(5) started\nfac(5)=120\n", out));
	}

	public void testPersonSyncPipeline() {
		LocalBrokerClient.getInstance("default-local").stop();
		testFile("ini/test/channels/person_sync_pipeline.ini",
				(p, out) -> assertEquals("Sending person Person[firstName=Jacques,lastName=Chirac] for enrichment\n"
						+ "Sending person Person[firstName=Barack,lastName=Obama] for enrichment\n"
						+ "Sending person Person[firstName=Albert,lastName=Einstein] for enrichment\n"
						+ "Skipping wrong person Person[firstName=Titi]\n"
						+ "Sending person Person[firstName=Unknown,lastName=Person] for enrichment\n"
						+ "Sending person Person[firstName=Edsger,lastName=Dijkstra,middleName=W.] for enrichment\n"
						+ "Wikipedia Enrichment for Jacques Chirac\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Jacques_Chirac\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Jacques_Chirac\n"
						+ "Wikipedia Enrichment for Barack Obama\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Barack_Obama\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Barack_Obama\n"
						+ "Wikipedia Enrichment for Albert Einstein\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Albert_Einstein\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Albert_Einstein\n"
						+ "Wikipedia Enrichment for Unknown Person\n" //
						+ "ERROR: https://en.wikipedia.org/wiki/Unknown_Person\n"
						+ "Wikipedia Enrichment for Edsger Dijkstra\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Edsger_W._Dijkstra\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Edsger_W._Dijkstra\n", out));
	}

	public void testPersonBackPressurePipeline() {
		testFile("ini/test/channels/person_back_pressure_pipeline.ini",
				(p, out) -> assertEquals("Sending person Person[firstName=Jacques,lastName=Chirac] for enrichment\n"
						+ "Wikipedia Enrichment for Jacques Chirac\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Jacques_Chirac\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Jacques_Chirac\n"
						+ "Sending person Person[firstName=Barack,lastName=Obama] for enrichment\n"
						+ "Wikipedia Enrichment for Barack Obama\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Barack_Obama\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Barack_Obama\n"
						+ "Sending person Person[firstName=Albert,lastName=Einstein] for enrichment\n"
						+ "Wikipedia Enrichment for Albert Einstein\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Albert_Einstein\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Albert_Einstein\n"
						+ "Skipping wrong person Person[firstName=Titi]\n"
						+ "Sending person Person[firstName=Unknown,lastName=Person] for enrichment\n"
						+ "Wikipedia Enrichment for Unknown Person\n"
						+ "ERROR: https://en.wikipedia.org/wiki/Unknown_Person\n"
						+ "Sending person Person[firstName=Edsger,lastName=Dijkstra,middleName=W.] for enrichment\n"
						+ "Wikipedia Enrichment for Edsger Dijkstra\n"
						+ "Fetching Wikipedia page: https://en.wikipedia.org/wiki/Edsger_W._Dijkstra\n"
						+ "Sending back enriched person for https://en.wikipedia.org/wiki/Edsger_W._Dijkstra\n"
						+ "Sent all persons\n", out));
	}

	public void testImplicitChannel1() {
		testFile("ini/test/channels/implicitChannel1.ini", (p, out) -> assertEquals("consumed = hello\nend\n", out));
	}

	public void testChannelOverload() {
		testFile("ini/test/channels/channel_overload.ini", (p, out) -> assertEquals("2.0\nstopped\n", out));
	}

}
