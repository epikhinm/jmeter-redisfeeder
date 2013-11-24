package me.schiz.jmeter.redisfeeder.functions;

import com.google.common.base.Charsets;
import me.schiz.jmeter.redisfeeder.config.RedisFeeder;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import redis.Command;
import redis.client.RedisClient;
import redis.reply.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RedisFunction  extends AbstractFunction {
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static final List<String> desc = new LinkedList<String>();

	private static final String KEY = "__redis"; //$NON-NLS-1$
	private static final String NULL = "null";

	static {
		desc.add("instance name");
		desc.add("command");
		desc.add("arguments");
	}
	private Object[] values;

	public RedisFunction() {
	}

	/** {@inheritDoc} */
	@Override
	public String execute(SampleResult previousResult, Sampler currentSampler)
			throws InvalidVariableException {
		String result = null;
		String instance;
		String command;
		Object[] args = null;
		synchronized (values) {
			instance = ((CompoundVariable)values[0]).execute().trim();
			command = ((CompoundVariable)values[1]).execute().trim();

			if(values.length > 2) {
				args = new Object[values.length - 2];
				for(int i =0;i<values.length - 2; ++i) {
					if(values[i+2] instanceof CompoundVariable) {
						args[i] = ((CompoundVariable)values[i+2]).execute().trim();
					}
				}
			}
		}

		RedisClient client = RedisFeeder.getInstance().getClient(instance);
		if(client == null) {
			return NULL;
		}

		Reply reply = client.execute(command, new Command(command.getBytes(Charsets.UTF_8), args));
		if(reply instanceof IntegerReply) result = String.valueOf(((IntegerReply)reply).data());
		if(reply instanceof ErrorReply) {
			result = ((ErrorReply)reply).data();
			log.error("error reply from redis: " + result);
		}
		if(reply instanceof BulkReply)  result = ((BulkReply)reply).asAsciiString();
		if(reply instanceof MultiBulkReply) {
			Reply[] replies = ((MultiBulkReply)reply).data();
			StringBuffer sb = new StringBuffer();
			for(int i=0;i>replies.length;++i) {
				sb.append(replies[i].data());
				sb.append(";");
			}
			result = sb.toString();
		}
		if(reply instanceof StatusReply)    result = ((StatusReply)reply).data();
		return result;

	}

	/*
	 * Helper method for use by scripts
	 *
	 */
	public void log_info(String s) {
		log.info(s);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {

		checkParameterCount(parameters, 2, 130);

		values = parameters.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public String getReferenceKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getArgumentDesc() {
		return desc;
	}

}