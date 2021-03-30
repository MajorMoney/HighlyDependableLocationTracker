package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
	private int gridX;
	private DatagramSocket socket;
	private InetAddress localHost;
	private Executor mailer;
	private ThreadPoolExecutor RequestPool;

	public BluetoothSimulation(int range, int port, int gridX) {
		this.range = range;
		this.port = port;
		this.gridX = gridX;
		openBluetoothConection();
		setUpThreadPools();
	}

	private void setUpThreadPools() {
		mailer = Executors.newSingleThreadExecutor();
		RequestPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		RequestPool.setCorePoolSize(1);
		RequestPool.setMaximumPoolSize(5);
	}

	private void openBluetoothConection() {
		try {
			localHost = InetAddress.getLocalHost();
			this.socket = new DatagramSocket(port);
			// this thread will continually listening to incoming requests
			BluetoothBroadcastListener listener = new BluetoothBroadcastListener(socket, BUFFER_SIZE, this);
			listener.start();
			System.out.printf("Emulating Bluetooth on port %d %n", port);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void closeBluetoothConection() {

	}

	public void changePort(int port) {
		closeBluetoothConection();
		this.port = port;
		openBluetoothConection();
	}

	public void sendBroadcastToNearby() throws IOException {
		if (port == 8080) {// if so serve para efeitos de teste
			String jsonString = "{" + "\"user\":\"User1\"," + "\"msgType\":\"LocationProofRequest\"," + "\"msgData\":{"
					+ "\"epoch\":\"teste\"," + "\"position\":\"" + port + "\"" + "}," + "\"apends\":\"[]\"" + "}";
			byte[] clientBuffer = jsonString.getBytes();
			// This for calculates the proximity ports, simulating IRL bluethooth
			// connections
			for (int i = port - (range * gridX); i <= port + (range * gridX); i += gridX) {
				for (int j = -range; j <= range; j++) {
					int serverPort = i + j;
					if (serverPort != port) {// Skips msg to self port,sera que ha um metodo mais eficiente??
						DatagramPacket clientPacket = new DatagramPacket(clientBuffer, clientBuffer.length, localHost,
								serverPort);
						socket.send(clientPacket);
						System.out.printf("Send to: %s:%d %n", localHost, serverPort);
					}
				}
			}
		}
	}

	// handles the callback of received messanges from listener
	public void callbackHandler(DatagramPacket clientpacket) {
		Runnable newTask = () -> {
			ReceivedMessagesHandler(clientpacket);
		};
		mailer.execute(newTask);
	}

	// in this method, "mailer" thread will split and distribute diferent type
	// messages to their respective handlers
	private void ReceivedMessagesHandler(DatagramPacket packet) {
		String packetText = new String(packet.getData(), 0, packet.getLength());
		JsonElement jsonTree = JsonParser.parseString(packetText).getAsJsonObject();

		if (jsonTree.isJsonObject()) {
			JsonObject msg = jsonTree.getAsJsonObject();
			String msgType = msg.get("msgType").getAsString();
			if (msgType.toString().equals("LocationProofRequest")) {
				Runnable task = () -> {
					LocationRequestHandler(packet);
				};
				RequestPool.execute(task);
			}

			if (msgType.equals("")) {

			}

			/*
			 * Just for reuse purposes||||||||||||||||||||||||||||||
			 * 
			 * JsonElement f2 = jsonObject.get("f2"); System.out.println("f2 value is " +
			 * f2); if (f2.isJsonObject()) { JsonObject f2Obj = f2.getAsJsonObject();
			 * 
			 * JsonElement f3 = f2Obj.get("f3"); System.out.println("f3 value is " +
			 * f3.getAsString()); }
			 */
		}

	}

	// é preciso testar com JSONs fdds maybe, para ver se causa problemas
	// Ou provavelente fazer uma função, check params,**actualização: maybe not
	private void LocationRequestHandler(DatagramPacket packet) {
		// checkJsonParams(packet);
		String packetText = new String(packet.getData(), 0, packet.getLength());
		JsonElement tree = JsonParser.parseString(packetText).getAsJsonObject();
		if (tree.isJsonObject()) {
			JsonObject msg = tree.getAsJsonObject();
			JsonObject msgData = msg.get("msgData").getAsJsonObject();
			if (msgData.isJsonObject()) {
				String epoch = msgData.get("epoch").getAsString();
				int position = msgData.get("position").getAsInt();
				// if->checkEpoch(epoch);
				if (checkPosition(position, packet.getPort())) {
					System.out.println(
							"JSON: " + " position:" + position + "->true" + "   epoch:" + epoch + "->true(IG,for now)");//mudar para enviar resposta
				}
			}
		}
	}

	private boolean checkPosition(int position, int srcPort) {
		if (position == srcPort) {
			for (int i = port - (range * gridX); i <= port + (range * gridX); i += gridX) {
				for (int j = -range; j <= range; j++) {
					if (position == i + j) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void LocationProofHandler() {

	}

	public void testSomething() {
	}

}
