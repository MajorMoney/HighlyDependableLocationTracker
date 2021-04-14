package pt.ist.stdf.UserProgram.User;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import pt.ist.stdf.CryptoUtils.CryptoUtils;
import pt.ist.stdf.ServerProgram.Position;
import pt.ist.stdf.ServerProgram.HandleClient.ClientMessage;
import pt.ist.stdf.ServerProgram.HandleClient.ClientMessage.ClientMessageTypes;
import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.Location.Location;
import pt.ist.stdf.UserProgram.User.ServerResponseListener.ServerResponseListener;

public class SimpleUser extends User {

	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;

	private final int REQUEST_VALDATION = 0;
	private final int RESPONSE_TO_VALIDATION = 1;
	private final int REPORT_SUBMISSION = 2;
	// Server messages
	private final int REPORT_OTHER_USER = 5;
	private final int OBTAIN_LOCATION_REPORT = 1;
	private final int SUBMIT_LOCATION_REPORT = 3;
	private final int OBTAIN_LOCATION_REPORT_HA = 4;
	private final int OBTAIN_USERS_AT_LOCATION_HA = 2;
	private final int SUBMIT_SHARED_KEY = 9;

//Test	
	private final int MAX_NUM_REPORTS = 3;

	private final int SERVER_PORT = 8888;

	private ServerResponseListener serverResponseListener;
	protected Location loc;
	protected Bluetooth bltth;

	private SimpleUserMessageHandler messageHandler;
	private ScheduledExecutorService timer;

	private HashMap<Integer, ReportList> openReportMakers;
	private List<Integer> sentReports;

	private byte[] buffer = new byte[BUFFER_SIZE];
	private Socket serverSocket;
	private DataInputStream in;
	private DataOutputStream out;

	

	public SimpleUser(String serverHost, int serverPort, Location loc, Bluetooth bltth, KeyPair kp,PublicKey serverPK) {
		super(serverHost, serverPort,kp,serverPK);
		this.loc = loc;
		this.bltth = bltth;
	
		
		openReportMakers = new HashMap<Integer, ReportList>();
		sentReports = Collections.synchronizedList(new ArrayList<Integer>());
		setUpTimer();
		openServerConnection(); // Temporariariament aqui
		messageHandler = new SimpleUserMessageHandler(this, bltth);
		messageHandler.start();
		System.out.println("User ID: "+getId()+" Was created at position " + loc.getCurrentLocation());

	}

	// ##Starter methods##//

	private void setUpTimer() {
		timer = Executors.newSingleThreadScheduledExecutor();
	}

	

	private void openServerConnection() {
		try {
			serverSocket = new Socket("localhost", SERVER_PORT);
			in = new DataInputStream(serverSocket.getInputStream());
			out = new DataOutputStream(serverSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void starServerListener(LinkedBlockingQueue<JsonObject> messages) throws IOException {
		serverResponseListener = new ServerResponseListener(serverSocket, messages);
		serverResponseListener.start();
	}

	// ##Utils##//

	private synchronized boolean checkSentReports(int msgId) {
		synchronized (sentReports) {
			if (sentReports.contains(msgId)) {
				return true;
			}
			return false;
		}
	}

	private synchronized void addSentReport(int msgId) {
		if (!checkSentReports(msgId)) {
			synchronized (sentReports) {
				sentReports.add(msgId);
				System.out.println("User ID:"+getId()+" Report Added with msgID: " + msgId + checkSentReports(msgId) + "\n ");
			}
		} else {
			System.out.println("User ID:"+getId()+" Strange, Report alredy sent with Id: " + msgId+"\n");
		}

	}

	private synchronized void removeSentReport(int msgId) {
		synchronized (sentReports) {
			sentReports.remove(sentReports.indexOf(msgId));
			System.out.println("User ID:"+getId()+" Report Destroyed with msgID: " + msgId + checkSentReports(msgId) + "\n ");
		}
	}

	private synchronized boolean checkReportMaker(int msgId) {
		boolean b;
		synchronized (openReportMakers) {
			b = openReportMakers.containsKey(msgId);
		}
		if (b) {
			return true;
		}
		return false;
	}


	private synchronized void tryAddToReportMaker(int msgId, JsonObject msg, ReportList rm) {
		synchronized (openReportMakers) {
			if (!checkReportMaker(msgId)) {
				openReportMakers.put(msgId, rm);
				openReportMakers.get(msgId).add(msg);
				startNewTimer(rm, msgId);
				System.out.println("User ID:"+getId()+" ReportMaker Created with msgID: " + msgId + "\n ");
			} else {
				openReportMakers.get(msgId).add(msg);
				System.out.println("User ID:"+getId()+" Report Added to ReportMaker with msgID: " + msgId  + "\n ");
			}

		}
	}

	private synchronized void removeReportMaker(int msgId) {
		synchronized (openReportMakers) {
			openReportMakers.remove(msgId);
		}
		System.out.println("User ID:"+getId()+" ReportMaker Destroyed with msgID: " + msgId + checkReportMaker(msgId) + "\n ");
	}

	// ###JSON Messages constructing functions##//

	// Generates Report to send other users
	private String generateLocationRequest() {

		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "1");
		msgData.add("position", loc.getCurrentLocationAsJsonArray());

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", REQUEST_VALDATION);
		obj.addProperty("userId", this.getId());
		obj.addProperty("msgId", (int) (Math.random() * 8945589));// pode se mudar a seed
		obj.add("msgData", msgData);

		addSentReport(obj.get("msgId").getAsInt());

		return obj.toString();
	}

	// Responds to a proof request
	public void respondLocationProof(JsonObject msg) {

		System.out.println("User ID:" + this.getId() + " received proof request from user ID:" + msg.get("userId")
				+ "\nMSG: " + msg.toString() + "\n");

		// if position valid
		// respond:

		// Change message body and send response
		msg.addProperty("msgType", RESPONSE_TO_VALIDATION);
		msg.addProperty("userId", Integer.toString(this.getId()));

		JsonObject msgData = new JsonObject();

		StringBuilder singature = new StringBuilder();
		singature.append("Assinado por: ");
		singature.append(this.getId());
		msgData.addProperty("singature,", singature.toString());

		msg.add("msgData", msgData);
		bltth.respondRequest(msg);

		// else
		// report to server

	}

	public JsonArray generateTestReports() {
		JsonArray jsonArr = new JsonArray();
		for (int i = 0; i < MAX_NUM_REPORTS; i++) {

			JsonObject msgData = new JsonObject();
			Random r = new Random();
			msgData.addProperty("epoch", 111 + r.nextInt(5));
			JsonObject obj = new JsonObject();
			obj.addProperty("msgType", REPORT_OTHER_USER);
			int low = 90;
			int high = 101;
			int result = r.nextInt(high - low) + low;
			obj.addProperty("userId", result);
			obj.add("msgData", msgData);
			jsonArr.add(obj);
		}
		return jsonArr;
	}

	// Generate report message for server
	public JsonObject generateSubmitLocationReport(JsonArray reports, int msgId) {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "111");
		msgData.addProperty("position", loc.getCurrentLocation());
		msgData.addProperty("num_reports", reports.size());
		msgData.add("reports", reports);

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", SUBMIT_LOCATION_REPORT);
		obj.addProperty("userId", getId());
		obj.addProperty("msgId", msgId);
		obj.add("msgData", msgData);

		return obj;
	}

	// Generate report message for server
	public JsonObject generateObtainLocationReport() {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "1");

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", OBTAIN_LOCATION_REPORT);
		Random r = new Random();
		int low = 1;
		int high = 5;
		int result = r.nextInt(high - low) + low;
		obj.addProperty("userId", result);
		obj.add("msgData", msgData);

		return obj;
	}

	// Generate report message for server
	public JsonObject generateObtainLocationReportHA() {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "1");
		msgData.addProperty("userId", 4);
		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", OBTAIN_LOCATION_REPORT_HA);
		Random r = new Random();
		int low = 90;
		int high = 101;
		int result = r.nextInt(high - low) + low;
		obj.addProperty("userId", result);
		obj.add("msgData", msgData);

		return obj;
	}

	public JsonObject generateSubmitSharedKey() {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("sharedKey", "thisisasharedkey");

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", SUBMIT_SHARED_KEY);
		Random r = new Random();
		int low = 1;
		int high = 5;
		int result = r.nextInt(high - low) + low;
		obj.addProperty("userId", result);
		obj.add("msgData", msgData);

		return obj;
	}

	public JsonObject generateObtainUsersAtLocationHA() {
		JsonObject msgData = new JsonObject();
		Random rand = new Random();
		GridLocation g = new GridLocation(4, 4);
		msgData.addProperty("position", g.getCurrentLocation());
		System.out.println("GRID LOCATION: " + g.getCurrentLocation());
		msgData.addProperty("epoch", 1);
		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", OBTAIN_USERS_AT_LOCATION_HA);
		Random r = new Random();
		int low = 90;
		int high = 101;
		int result = r.nextInt(high - low) + low;
		obj.addProperty("userId", result);
		obj.add("msgData", msgData);

		return obj;
	}


	// ##General Procedure Methods##//

	// Broadcast Request to other users
	public void requestLocationProof() {
		String msg = generateLocationRequest();
		try {
			System.out.println("User ID: " + this.getId() + " requested proof of identification");
			bltth.sendBroadcastToNearby(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Handle a response to a proof previously submitted
	public void hadleResponseMessage(JsonObject msg) {
		msg.remove("senderPort");

		System.out.println("User ID:" + this.getId() + " received response to proof request! Sender ID:"
				+ msg.get("userId") + "\n\"MSG:" + msg.toString() + "\n");

		int msgId = msg.get("msgId").getAsInt();
		if (checkSentReports(msgId)) {
				ReportList rm = new ReportList(msgId);
				tryAddToReportMaker(msgId, msg, rm);		
		}
	}

	public void handleServerResponseObtainLocRepMessage(JsonObject msg) {
		int userId = msg.get("userId").getAsInt();
		JsonObject msgData = msg.get("msgData").getAsJsonObject();
		int epoch = msgData.get("epoch").getAsInt();
		String[] positionString1 = msgData.get("position").getAsString().split(" ");
		GridLocation gp = new GridLocation((int) positionString1[0].charAt(positionString1[0].length() - 1),
				(int) positionString1[1].charAt(positionString1[1].length() - 1));
		System.out.println("[CLIENT FINAL: OBTAINS USER LOCATION " + userId + " epoch asked: " + epoch
				+ "->> position returned: " + gp.getCurrentLocation());

	}

	public void handleServerResponseObtainUsersAtLocationMessage(JsonObject msg) {
		int userId = msg.get("userId").getAsInt();
		JsonObject msgData = msg.get("msgData").getAsJsonObject();
		int epoch = msgData.get("epoch").getAsInt();
		String[] positionString1 = msgData.get("position").getAsString().split(" ");
		GridLocation gp = new GridLocation((int) positionString1[0].charAt(positionString1[0].length() - 1),
				(int) positionString1[1].charAt(positionString1[1].length() - 1));
		JsonArray users = msgData.get("users").getAsJsonArray();
		for (int i = 0; i < users.size(); i++) {
			System.out.println("Found user:" + users.get(i).getAsInt());
		}

		System.out.println("[CLIENT FINAL: OBTAINS USERS AT LOCATION " + userId + " epoch asked: " + epoch
				+ "->> position returned: " + gp.getCurrentLocation());

	}

	public void handleServerResponseObtainLocationMessageHA(JsonObject msg) {
		int userId = msg.get("userId").getAsInt();
		JsonObject msgData = msg.get("msgData").getAsJsonObject();
		int epoch = msgData.get("epoch").getAsInt();
		String[] positionString1 = msgData.get("position").getAsString().split(" ");
		GridLocation gp = new GridLocation(
				Integer.parseInt(String.valueOf(positionString1[0].charAt(positionString1[0].length() - 1))),
				Integer.parseInt(String.valueOf(positionString1[1].charAt(positionString1[1].length() - 1))));
		System.out.println("[CLIENT HA FINAL: OBTAINS USER LOCATION " + userId + " epoch asked: " + epoch
				+ "->> position returned: " + gp.getCurrentLocation());

	}

	private void startNewTimer(ReportList rm, int msgId) {
		timer.schedule(new Runnable() {
			public void run() {
				try {
					removeSentReport(msgId);
					removeReportMaker(msgId);
					submitLocationReport(generateSubmitLocationReport(rm.getAllReports(), msgId));
					System.out.println("User ID:" + getId() + " Sent Location Request With Proof Reports \n"
							+ generateSubmitLocationReport(rm.getAllReports(), msgId).toString() + "\n");
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 5000, TimeUnit.MILLISECONDS);
	}

	public void submitLocationReport(JsonObject j) {
		try {
			out.write(j.toString().getBytes(StandardCharsets.UTF_8));
			out.flush();
			// out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeServerConnection() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	// Classe a chamar no main para simular o user
//	public void testSomethingWithServer() {
//
//		try {
//			Socket s = new Socket("localhost", SERVER_PORT);
//			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//			JsonObject j = submitLocationReport();
//
//			System.out.println(j.toString());
//
//			dout.write(j.toString().getBytes(StandardCharsets.UTF_8));
//			dout.flush();
//			dout.close();
//			s.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
