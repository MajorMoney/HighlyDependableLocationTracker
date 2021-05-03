package pt.ist.stdf.HighlyDependableTracker.UserProgram.Bluetooth;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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

	public synchronized void changeSocket(DatagramSocket socket) {
        this.socket = socket;
        notify();
        if (socket==null) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	//Listens for Bluetooth messages
	//todo udp broadcast is not reliable so we should maybe insist podemos ver a grid para ver proximidade- se houverem useres proximos entao dar broadcast até receber os reports port exemplo
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
                changeSocket(null);
                System.out.println("wakeup");
                if(socket==null) {
                    System.out.println("ERROR ON BLUETOOTH");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
	}

}
