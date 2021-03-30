package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;



public class BluetoothBroadcastListener extends Thread {

	private BluetoothSimulation master;
	private DatagramSocket socket;
	private int bufferSize;

	public BluetoothBroadcastListener(DatagramSocket socket, int bufferSize, BluetoothSimulation master) {
		this.socket = socket;
		this.bufferSize = bufferSize;
		this.master = master;
	}

	//listen to messages continuoslly and callback master when one is received
	@Override
	public void run() {
		byte[] buf = new byte[bufferSize];
		while(true) {
			DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(clientPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			master.callbackHandler(clientPacket);
		}
	}

}
