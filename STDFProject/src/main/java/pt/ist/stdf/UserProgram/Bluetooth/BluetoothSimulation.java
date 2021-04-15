package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/* 
 * This class emulates Bluetooth with UDP sockets
 */
public class BluetoothSimulation implements Bluetooth {

	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;

	private final int range;

	private int port;
	private int basePort;
	private int limitX;
	private int limitY;
	
	private BluetoothListener listener;
	private DatagramSocket socket;
	private InetAddress localHost;

	public BluetoothSimulation(int range, int port, int basePort, int limitX, int limitY) {
		this.range = range;
		this.port=port;
		this.basePort=basePort;
		this.limitX = limitX;
		this.limitY = limitY;
		try {
			this.localHost=InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		openSocket(port);
	}
	
	private void openSocket(int port) {
		try {
			this.socket = new DatagramSocket(port);
			System.out.println("Simulation bluetooth on port: "+port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void configureListner(LinkedBlockingQueue messages) {

		openBluetoothConection(messages);
		
	}

	private void openBluetoothConection(LinkedBlockingQueue messages) {

        this.listener = new BluetoothListener(socket, BUFFER_SIZE, messages);
        this.listener.start();

    }
	
	public void changePort(int port) {
        this.port = port;
        this.socket.close();
        openSocket(port);
        this.listener.changeSocket(this.socket);
    }
	
	public void sendBroadcastToNearby(String msg) throws IOException {
		byte[] clientBuffer = msg.getBytes();
		int sendPort;
		int y = (port-basePort)/limitX;
		int x = (port-basePort)%limitX;
			for(int j=y==0?y:y-range; j<=y+range && j<limitY ; j++){
				for(int i=x==0?x:x-range; i<=x+range && i<limitX; i++) {
					sendPort=basePort+j*limitX+i;
					if (sendPort != port) {
						DatagramPacket clientPacket = new DatagramPacket(clientBuffer, clientBuffer.length, localHost, sendPort);
						socket.send(clientPacket);
					}
				}
			}
		}

	public void respondRequest(JsonObject msg) {
		
		int sendPort = msg.get("senderPort").getAsInt();
		msg.remove("senderPort");
		byte[] clientBuffer = (new Gson().toJson(msg)).getBytes();
		DatagramPacket clientPacket = new DatagramPacket(clientBuffer, clientBuffer.length, localHost, sendPort);
		try {
			socket.send(clientPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


//	// handles the callback of received messanges from listener
//	public void callbackHandler(DatagramPacket clientpacket) {
//		Runnable newTask = () -> {
//			ReceivedMessagesHandler(clientpacket);
//		};
//		mailer.execute(newTask);
//	}
//
//	// in this method, "mailer" thread will split and distribute diferent type
//	// messages to their respective handlers
//	private void ReceivedMessagesHandler(DatagramPacket packet) {
//		String packetText = new String(packet.getData(), 0, packet.getLength());
//		JsonElement jsonTree = JsonParser.parseString(packetText).getAsJsonObject();
//
//		if (jsonTree.isJsonObject()) {
//			JsonObject msg = jsonTree.getAsJsonObject();
//			String msgType = msg.get("msgType").getAsString();
//			if (msgType.toString().equals("LocationProofRequest")) {
//				Runnable task = () -> {
//					LocationRequestHandler(packet);
//				};
//				RequestPool.execute(task);
//			}
//
//			if (msgType.equals("")) {
//
//			}
//
//			/*
//			 * Just for reuse purposes||||||||||||||||||||||||||||||
//			 * 
//			 * JsonElement f2 = jsonObject.get("f2"); System.out.println("f2 value is " +
//			 * f2); if (f2.isJsonObject()) { JsonObject f2Obj = f2.getAsJsonObject();
//			 * 
//			 * JsonElement f3 = f2Obj.get("f3"); System.out.println("f3 value is " +
//			 * f3.getAsString()); }
//			 */
//		}
//
//	}
//
//	// é preciso testar com JSONs fdds maybe, para ver se causa problemas
//	// Ou provavelente fazer uma função, check params,**actualização: maybe not
//	private void LocationRequestHandler(DatagramPacket packet) {
//		// checkJsonParams(packet);
//		String packetText = new String(packet.getData(), 0, packet.getLength());
//		JsonElement tree = JsonParser.parseString(packetText).getAsJsonObject();
//		if (tree.isJsonObject()) {
//			JsonObject msg = tree.getAsJsonObject();
//			JsonObject msgData = msg.get("msgData").getAsJsonObject();
//			if (msgData.isJsonObject()) {
//				String epoch = msgData.get("epoch").getAsString();
//				int position = msgData.get("position").getAsInt();
//				// if->checkEpoch(epoch);
//				if (checkPosition(position, packet.getPort())) {
//					System.out.println(
//							"JSON: " + " position:" + position + "->true" + "   epoch:" + epoch + "->true(IG,for now)");//mudar para enviar resposta
//				}
//			}
//		}
//	}
//
//	//Checka se está na range
//	private boolean checkPosition(int position, int srcPort) {
//		if (position == srcPort) {
//			for (int i = port - (range * gridX); i <= port + (range * gridX); i += gridX) {
//				for (int j = -range; j <= range; j++) {
//					if (position == i + j) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	//Vai dar handle ás responses, vai precisar de uma pool/threads personalizadas
//	//Tem que iplementar timeout, e serem concorrentes para dar os apends
//	private void LocationProofHandler() {
//
//	}
//
//	public void testSomething() {
//	}


}
