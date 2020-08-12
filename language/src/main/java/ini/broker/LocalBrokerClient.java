package ini.broker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import ini.IniContext;
import ini.IniLanguage;

public class LocalBrokerClient implements BrokerClient {

	/* LocalBrokerClient is a Singleton */
	private static LocalBrokerClient instance;
	private String name;
	private ChannelConfiguration defaultChannelConfiguration;
	private Map<String, BlockingQueue<Object>> channels = new Hashtable<>();
	private Map<String, Collection<String>> channelConsumers = new Hashtable<>();
	private Map<String, Thread> consumers = new Hashtable<>();

	synchronized public static LocalBrokerClient getInstance(String name) {
		if (instance == null) {
			IniLanguage.LOGGER.debug("creating local broker client");
			instance = new LocalBrokerClient(name);
		}
		return instance;
	}

	private LocalBrokerClient(String name) {
		this.name = name;
		this.defaultChannelConfiguration = new ChannelConfiguration(1);
	}

	@Override
	public ChannelConfiguration getDefaultChannelConfiguration() {
		return defaultChannelConfiguration;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	@TruffleBoundary
	synchronized public void stopConsumer(String consumerId) {
		if (consumers.containsKey(consumerId)) {
			IniLanguage.LOGGER.debug("stopping consumer " + consumerId);
			consumers.get(consumerId).interrupt();
			consumers.remove(consumerId);
		}
	}

	@TruffleBoundary
	synchronized private Collection<String> getOrCreateChannelCustomerIds(String channel) {
		Collection<String> consumerIds = channelConsumers.get(channel);
		if (consumerIds == null) {
			consumerIds = new ArrayList<>();
			channelConsumers.put(channel, consumerIds);
		}
		return consumerIds;
	}

	@Override
	public void stopConsumers(String channelName) {
		Collection<String> consumerIds = getOrCreateChannelCustomerIds(channelName);
		IniLanguage.LOGGER.debug("stopping consumers for channel " + channelName);
		for (String consumerId : consumerIds) {
			stopConsumer(consumerId);
		}
	}

	@Override
	@TruffleBoundary
	synchronized public boolean isConsumerRunning(String consumerId) {
		return consumers.containsKey(consumerId);
	}

	private ChannelConfiguration getConfiguration(Channel<?> channel) {
		return channel.getConfiguration() == null ? defaultChannelConfiguration : channel.getConfiguration();
	}

	@SuppressWarnings("unchecked")
	@TruffleBoundary
	synchronized private <T> BlockingQueue<T> getOrCreateChannel(Channel<T> channel) {
		BlockingQueue<Object> channelQueue = channels.get(channel.getName());
		if (channelQueue == null) {
			if (channel.getConfiguration() != null) {
				channel.getConfiguration().setParentConfiguration(defaultChannelConfiguration);
			}
			IniLanguage.LOGGER.debug("create channel " + channel + " / " + getConfiguration(channel).getSize());
			channels.put(channel.getName(),
					channelQueue = new LinkedBlockingQueue<>(getConfiguration(channel).getSize()));
		}
		return (BlockingQueue<T>) channelQueue;
	}

	@Override
	@TruffleBoundary
	public <T> String consume(Channel<T> channel, Function<T, Boolean> consumeHandler, IniContext context) {
		if (channel == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		String id = channel + "-" + UUID.randomUUID().toString();
		getOrCreateChannelCustomerIds(channel.getName()).add(id);

		IniLanguage.LOGGER.debug("consumer polling from topic '" + channel + "'...");

		Runnable runnable = new Thread() {
			public void run() {
				while (true && !Thread.interrupted()) {
					try {
						BlockingQueue<T> queue = getOrCreateChannel(channel);
						T data = queue.poll(10000, TimeUnit.MILLISECONDS);
						if (data != null) {
							IniLanguage.LOGGER.debug("consumed from '" + channel + "': " + data);
							boolean result = true;
							if (consumeHandler != null) {
								result = consumeHandler.apply(data);
							}
							if(!result) {
								// push back the rejected data
								queue.put(data);
							}
						}
					} catch (InterruptedException e) {
						IniLanguage.LOGGER.debug("woke up consumer for " + channel);
						break;
					} finally {
					}
				}
				IniLanguage.LOGGER.debug("consumer '" + id + "' out of consume loop");
				getOrCreateChannelCustomerIds(channel.getName()).remove(id);
				consumers.remove(id);
				IniLanguage.LOGGER.debug("consumer '" + id + "' closed");
			}
		};
		IniLanguage.LOGGER.debug("Creating a Thread in LocalBrokerClient");
		Thread thread = context.getEnv().createThread(runnable, context.getEnv().getContext());
		context.startedThreads.add(thread);
		
		consumers.put(id, thread);

		thread.start();

		return id;

	}

	@Override
	public <T> void produce(Channel<T> channel, T data) {
		try {
			IniLanguage.LOGGER.debug("producing on channel " + channel + " - data=" + data);
			getOrCreateChannel(channel).put(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	synchronized public void stop() {
		for (String channelName : channels.keySet()) {
			stopConsumers(channelName);
		}
		instance = null;
	}
}
