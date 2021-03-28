package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/* 
 * This class emulates Bluetooth with UDP sockets
 */
public class BluetoothSimulation implements Bluetooth{
	
	private final int range;
	private int port;
	private DatagramSocket socket;
	
	
	public BluetoothSimulation(int range, int port) {
		this.range = range;
		this.port = port;
	}
	
	public void openBluetoothConection() {
		
		try {
			DatagramSocket socket = new DatagramSocket(port);
			System.out.printf("Emulating Bluetooth on port %d %n", port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void closeBluetoothConection() {
		
	}
	
	public void changePort(int port) {
		closeBluetoothConection();
		this.port=port;
		openBluetoothConection();
	}

	public void sendBroadcastToNearby() {
		// TODO Auto-generated method stub
		
	}	

}
