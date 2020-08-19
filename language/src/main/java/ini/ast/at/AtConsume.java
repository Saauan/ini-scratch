package ini.ast.at;

import java.io.PrintStream;

import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import ini.IniContext;
import ini.IniLanguage;
import ini.ast.ChannelDeclaration;
import ini.ast.ChannelDeclaration.Visibility;
import ini.broker.BrokerClient;
import ini.broker.Channel;

public class AtConsume extends At {

	Thread mainThread;
	ChannelDeclaration channel;
	BrokerClient brokerClient;
	Thread ruleThread;
	Integer stop;
	int stopCount = 0;
	String consumerId;
	// if true will reject new messages
	boolean stopping = false;

	@Override
	public void executeVoid(VirtualFrame frame) {
		IniContext context = lookupContextReference(IniLanguage.class).get();
		Env env = context.getEnv();

		// Set up the channelDeclaration
		channel = (getInContext().get("channel") == null ? (ChannelDeclaration) getInContext().get("from")
				: (ChannelDeclaration) getInContext().get("channel"));

		// Set up the broker
		brokerClient = BrokerClient.getDefaultInstance(channel.visibility == Visibility.GLOBAL);

		// Set up the stop integer
		stop = getInContext().get("stop") == null ? 1 : (int) getInContext().get("stop");
		
		// Create the consumer
		consumerId = brokerClient
				.consume(new Channel<Object>(channel.mappedName, null, channel.getChannelConfiguration()), value -> {
					if (stopping) {
						return false;
					}
					// If the value is a stop message
					if (ChannelDeclaration.STOP_MESSAGE.equals(value)) {
						IniLanguage.LOGGER.debug("recieved stop message: " + AtConsume.this);
						stopCount++;
						if (stop != 0 && stopCount >= stop) {
							stopping = true;
							isEmptyQueue();
							IniLanguage.LOGGER.debug("stopping " + AtConsume.this);
							terminate();
						}
					} else {
						/* add the first outParameter to the frame */
						if (!getAtPredicate().outParameters.isEmpty()) {
							// TODO optimize according to type of value
							String name = getAtPredicate().outParameters.get(0).toString();
							FrameSlot slot = frame.getFrameDescriptor().findOrAddFrameSlot(name);
							frame.setObject(slot, value);
						}
						IniLanguage.LOGGER.debug("starting event thread for " + AtConsume.this);
						// Set up the ruleThread
						createAndRunRuleThread(frame, this, env, context);
					}
					return true;
				}, context);

	}

	@Override
	public void terminate() {
		brokerClient.stopConsumer(consumerId);
		stopping = true;
		super.terminate();
	}

	@Override
	public void prettyPrint(PrintStream out) {
		// TODO Auto-generated method stub

	}

}
