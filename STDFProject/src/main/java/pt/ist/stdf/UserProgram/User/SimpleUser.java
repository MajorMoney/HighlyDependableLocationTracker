package pt.ist.stdf.UserProgram.User;

import java.net.InetAddress;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.Location;

public class SimpleUser extends User{
	
	private Location loc;
	private Bluetooth bltth;
	
	public SimpleUser(String serverHost, int serverPort, Location loc, Bluetooth bltth) {
		super(serverHost, serverPort);
		this.loc = loc;
		this.bltth = bltth;
	}
	
	public void requestLocationProof() {
		
	}
	
	public void submitLocationRport() {
		
	}
	
	public void obtaionLocationReport() {
		
	}

}
