package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.at.At;

/**
 * aka KillAt
 *
 */
@NodeInfo(shortName = "stop")
@GenerateNodeFactory
public abstract class StopFunction extends BuiltInExecutable {

	public StopFunction() {	
	}
	
	@Specialization
	public boolean stop(At atToTerminate) {
		atToTerminate.terminate();
		return true;
	}

//	@Specialization
//	public void stop(ChannelDeclaration channelToTerminate) {
//		ChannelDeclaration channel = (ChannelDeclaration) target;
//		try {
////			BrokerClient.getDefaultInstance(eval.parser.env, channel.visibility == Visibility.GLOBAL).produce(
////					new Channel<>(channel.mappedName, Data.class, channel.getChannelConfiguration()),
////					ChannelDeclaration.STOP_MESSAGE);
//			throw new UnsupportedOperationException("Not implemented yet");
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
}
