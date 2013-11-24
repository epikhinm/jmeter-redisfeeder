package me.schiz.jmeter.redisfeeder.config;

import org.apache.jmeter.testbeans.BeanInfoSupport;

import java.beans.PropertyDescriptor;

public class RedisFeederBeanInfo extends BeanInfoSupport {
	protected RedisFeederBeanInfo() {
		super(RedisFeeder.class);

		createPropertyGroup("options", new String[]{
				"instanceName",
				"host",
				"port",
		});

		PropertyDescriptor p = property("instanceName");
		p.setValue(NOT_UNDEFINED, Boolean.FALSE);
		p.setValue(DEFAULT, RedisFeeder.DEFAULT_INSTANCE_NAME);

		p = property("host");
		p.setValue(NOT_UNDEFINED, Boolean.FALSE);
		p.setValue(DEFAULT, RedisFeeder.DEFAULT_HOST);

		p = property("port");
		p.setValue(NOT_UNDEFINED, Boolean.FALSE);
		p.setValue(DEFAULT, RedisFeeder.DEFAULT_PORT);
	}
}
