package pt.ist.stdf.UserProgram.User;

import java.io.IOException;
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
	
	//Classe a chamar no main para simular o user
	public void testSomething() {
		
		try {
			Thread.sleep(3000);
				bltth.sendBroadcastToNearby();		
		} catch (InterruptedException e2) {	
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
