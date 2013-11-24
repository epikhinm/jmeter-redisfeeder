package me.schiz.jmeter.redisfeeder.config;

import me.schiz.jmeter.redisfeeder.Configuration;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import redis.client.RedisClient;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class RedisFeeder
		extends ConfigTestElement
		implements TestStateListener, TestBean {
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static volatile RedisFeeder instance = null;
	private ConcurrentHashMap<String, Configuration> configurations;
	private ConcurrentHashMap<String, RedisClient> clients;

	protected static String INSTANCE_NAME = "me.schiz.jmeter.redisfeeder.instanceName";
	protected static String HOST = "me.schiz.jmeter.redisfeeder.host";
	protected static String PORT = "me.schiz.jmeter.redisfeeder.port";

	public static String DEFAULT_INSTANCE_NAME = "default";
	public static String DEFAULT_HOST = "localhost";
	public static int DEFAULT_PORT = 6379;

	public RedisFeeder() {
		if(instance == null) {
			synchronized (RedisFeeder.class) {
				if(instance == null)	instance = this;
			}
		}
		configurations = new ConcurrentHashMap<String, Configuration>();
		clients = new ConcurrentHashMap<String, RedisClient>();
	}

	public String getInstanceName() {
		return getPropertyAsString(INSTANCE_NAME);
	}

	public void setInstanceName(String name) {
		setProperty(INSTANCE_NAME, name, DEFAULT_INSTANCE_NAME);
	}

	public String getHost() {
		return getPropertyAsString(HOST);
	}

	public void setHost(String host) {
		setProperty(HOST, host, DEFAULT_HOST);
	}

	public int getPort() {
		return getPropertyAsInt(PORT);
	}

	public void setPort(int port) {
		setProperty(PORT, port, DEFAULT_PORT);
	}

	public RedisFeeder getInstance() {
		return instance;
	}

	public RedisClient getClient(String client) {
		long tid = Thread.currentThread().getId();
		String _client = client + "_" + String.valueOf(tid);
		RedisClient redisClient = clients.get(_client);
		if(redisClient == null) {
			Configuration config = configurations.get(client);
			if(config != null) {
				// Create new thread-local redis client
				try {
					redisClient = new RedisClient(config.host, config.port);
					clients.put(_client, redisClient);
					log.info("created new redis client for tid[" + tid + "] with configuration `" + client + "`");
				} catch (IOException e) {
					log.error("can't create new redis client for tid[" + tid + "] with configuration `" + client + "`", e);
					redisClient = null;
				}
			} else {
				log.error("not found configuration `" + client + "`");
			}
		}
		return redisClient;
	}

	@Override
	public void testStarted() {
		Configuration configuration = new Configuration(getHost(), getPort());
		Configuration prev = getInstance().configurations.putIfAbsent(getInstanceName(), configuration);
		if(prev != null) {
			log.warn("configuration `" + getInstanceName() + "` already created");
		} else {
			log.info("configuration `" + getInstanceName() + "` created");
		}
	}

	@Override
	public void testStarted(String s) {
		testStarted();
	}

	@Override
	public void testEnded() {
		for(String client : clients.keySet()) {
			try {
				clients.get(client).close();
				log.info("client `" + client + "` closed");
			} catch (IOException e) {
				log.warn("can't close client `" + client + "`", e);
			}
		}
		clients.clear();
		configurations.clear();
		log.info("instance `" + getInstanceName() + "` cleaned");
	}

	@Override
	public void testEnded(String s) {
		testEnded();
	}
}
