package me.schiz.jmeter.redisfeeder;

public class Configuration {
	public String host;
	public int port;
	/* support 0.8
	public int db;
	public String password;
	*/

	public Configuration() {}
	public Configuration(String host, int port) {
		this.host = host;
		this.port = port;
	}
}
