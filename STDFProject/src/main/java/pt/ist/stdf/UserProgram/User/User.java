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
		getIdFromServer();
	}
	
	protected int getId() {
		return id;
	}
	
	private void connectServer() {
		
	}
	
	private void getIdFromServer() {
		this.id=(int)(Math.random()*100);
	}

	
	public String getServerIp() {
		return serverHost;
	}
	
	public int getServerPort() {
		return serverPort;
	}

}
