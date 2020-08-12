package ini.eval.function;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import ini.ast.ChannelDeclaration;
import ini.ast.ChannelDeclaration.Visibility;
import ini.broker.BrokerClient;
import ini.broker.Channel;

@NodeInfo(shortName="produce")
@GenerateNodeFactory
public abstract class ProduceFunction extends BuiltInExecutable {

	// "produce", "channel", "data"
	public ProduceFunction() {
		super();
//		setDefaultValue(1, new StringLiteral("VOID"));
	}

	@Specialization
	public Object produce(ChannelDeclaration channel, Object data) {
		try {
			BrokerClient.getDefaultInstance(channel.visibility == Visibility.GLOBAL).produce(
					new Channel<>(channel.mappedName, null, channel.getChannelConfiguration()),
					data); // BUG : Copy data instead of passing by reference
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return channel;
	}

}
