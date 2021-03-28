package pt.ist.stdf.UserProgram.User;

import java.net.InetAddress;

public abstract class User {
	
	private int id;
	final String serverHost;
	final int serverPort;
	
	public User(String serverHost, int serverPort) {
		this.serverHost=serverHost;
		this.serverPort = serverPort;
		connectServer();
	}
	
	private void connectServer() {
		
	}
	
	public String getServerIp() {
		return serverHost;
	}
	
	public int getServerPort() {
		return serverPort;
	}

}
