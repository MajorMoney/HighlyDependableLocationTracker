package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class BluetoothListener extends Thread {

	private LinkedBlockingQueue<JsonObject> messages;
	private DatagramSocket socket;
	private int bufferSize;

	public BluetoothListener(DatagramSocket socket, int bufferSize, LinkedBlockingQueue<JsonObject> messages) {
		this.socket = socket;
		this.bufferSize = bufferSize;
		this.messages = messages;
	}

	public void changeSocket(DatagramSocket socket) {
		this.socket = socket;
	}
	
	//Listens for Bluetooth messages
	@Override
	public void run() {
		byte[] buf = new byte[bufferSize];
		String packetText;
		while(true) {
			DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(clientPacket);
				packetText = new String(clientPacket.getData(), 0, clientPacket.getLength());
				JsonElement jsonTree = JsonParser.parseString(packetText).getAsJsonObject();
				if (jsonTree.isJsonObject()) {
					JsonObject msg = jsonTree.getAsJsonObject();
					msg.addProperty("senderPort", clientPacket.getPort());
					messages.put(msg);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
