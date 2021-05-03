package pt.ist.stdf.UserProgram.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.stdf.CryptoUtils.CryptoUtils;
import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.Location.Location;
import pt.ist.stdf.UserProgram.User.ServerResponseListener.ServerResponseListener;
import pt.ist.stdf.constants.BluetoothMessageTypes;
import pt.ist.stdf.constants.ClientMessageTypes;

public class SimpleUser extends User {

	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;

	// Server messages

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

	private int epoch;
	private byte[] buffer = new byte[BUFFER_SIZE];
	private Socket serverSocket;
	private DataInputStream in;
	private DataOutputStream out;

	public SimpleUser(String serverHost, int serverPort, Location loc, Bluetooth bltth, KeyPair kp, PublicKey serverPK,
			int id) {
		super(serverHost, serverPort, kp, serverPK, id);
		this.loc = loc;
		this.bltth = bltth;
		this.epoch = 1;

		openReportMakers = new HashMap<Integer, ReportList>();
		sentReports = Collections.synchronizedList(new ArrayList<Integer>());
		setUpTimer();
		openServerConnection(); // Temporariariament aqui
		messageHandler = new SimpleUserMessageHandler(this, bltth);
		messageHandler.start();
		System.out.println("User ID: " + getId() + " Was created at position " + loc.getCurrentLocation()
				+ " with keypair" + kp.toString() + "Key server" + serverPK.toString());

	}

	// ##Starter methods##//

	public void setEpoch(int ep) {
		this.epoch = ep;
	}

	public int getEpoch() {
		return epoch;
	}

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
			}
		} else {
			System.out.println("User ID:" + getId() + " Strange, Report alredy sent with Id: " + msgId + "\n");
		}

	}

	private synchronized void removeSentReport(int msgId) {
		synchronized (sentReports) {
			sentReports.remove(sentReports.indexOf(msgId));
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
			} else {
				openReportMakers.get(msgId).add(msg);
			}

		}
	}

	private synchronized void removeReportMaker(int msgId) {
		synchronized (openReportMakers) {
			openReportMakers.remove(msgId);
		}
	}

	// ###JSON Messages constructing functions##//

	// Generates Report to send other users
	private String generateLocationRequest() {

		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", epoch);
		msgData.addProperty("position", loc.getCurrentLocation());

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", BluetoothMessageTypes.REQUEST_VALIDATION.getValue());
		obj.addProperty("userId", this.getId());
		obj.addProperty("msgId", (int) (Math.random() * 8945589));// pode se mudar a seed
		obj.add("msgData", msgData);

		addSentReport(obj.get("msgId").getAsInt());

		return obj.toString();
	}

	// Responds to a proof request
	private static StringBuilder getMimeBuffer() {
		StringBuilder buffer = new StringBuilder();
		for (int count = 0; count < 10; ++count) {
			buffer.append(UUID.randomUUID().toString());
		}
		return buffer;
	}

	public void respondLocationProof(JsonObject msg) {

		System.out.println("User ID:" + this.getId() + " received proof request from user ID:" + msg.get("userId")
				+ "\nMSG: " + msg.toString() + "\n");
		// Check position();
		msg.addProperty("msgType", BluetoothMessageTypes.RESPONSE_TO_VALIDATION.getValue());
		msg.addProperty("userId", Integer.toString(this.getId()));

		JsonObject msgData = (JsonObject) msg.get("msgData");
		// addPropertyepech
		msgData.addProperty("position", loc.getCurrentLocation());
		msgData.addProperty("signer", getId());
		try {
			// char[] padding = { '=' };
//			byte[] signature =msgData.toString().getBytes();
			// CryptoUtils.preHash(signature);
			String s = msgData.toString();
			String signedData = CryptoUtils.sign(s, getKp().getPrivate());
//			signature=CryptoUtils.signMessageRSA(signature, getKp().getPrivate());
//			byte[] encodedAsBytes = signature;
//			String encodedMime = Base64.getMimeEncoder().withoutPadding().encodeToString(encodedAsBytes);
			// String returnValue = new
			// String(Base64.getUrlEncoder().encode(signature)).substring(0,new String(
			// Base64.getUrlEncoder().encode(signature)).length()-2).replace('+',
			// '-').replace('/', '_');
			// new String(signature)
			msg.addProperty("signature", signedData);
			msg.addProperty("pk", getKp().getPublic().toString());
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		bltth.respondRequest(msg);

		// else
		// report to server

	}

//	public JsonArray generateTestReports() {
//		JsonArray jsonArr = new JsonArray();
//		for (int i = 0; i < MAX_NUM_REPORTS; i++) {
//
//			JsonObject msgData = new JsonObject();
//			Random r = new Random();
//			msgData.addProperty("epoch", epoch + r.nextInt(5));
//			JsonObject obj = new JsonObject();
//			obj.addProperty("msgType", ClientMessageTypes.userReport.getValue());
//			int low = 90;
//			int high = 101;
//			int result = r.nextInt(high - low) + low;
//			obj.addProperty("userId", result);
//			obj.add("msgData", msgData);
//			jsonArr.add(obj);
//		}
//		return jsonArr;
//	}

	// Generate report message for server
	public JsonObject SubmitLocationReport(JsonArray reports, int msgId) {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", epoch);
		msgData.addProperty("position", loc.getCurrentLocation());
		msgData.addProperty("num_reports", reports.size());
		msgData.add("reports", reports);

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", ClientMessageTypes.REPORT_SUBMISSION.getValue());
		obj.addProperty("userId", getId());
		obj.addProperty("msgId", msgId);
		obj.add("msgData", msgData);

		return obj;
	}

//	// Generate report message for server
//	public JsonObject generateObtainLocationReport() {
//		JsonObject msgData = new JsonObject();
//		msgData.addProperty("epoch", epoch);
//
//		JsonObject obj = new JsonObject();
//
//		obj.addProperty("msgType", ClientMessageTypes.obtainLocationReport.getValue());
//		Random r = new Random();
//		int low = 1;
//		int high = 5;
//		int result = r.nextInt(high - low) + low;
//		obj.addProperty("userId", result);
//		obj.add("msgData", msgData);
//
//		return obj;
//	}

	public JsonObject generateSubmitSharedKey()
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		JsonObject msg = new JsonObject();
		Gson gson = new Gson();
		msg.addProperty("userId", getId());
		msg.addProperty("msgType", ClientMessageTypes.submitSharedKey.getValue());

		SecretKey key = CryptoUtils.generateKeyAES();
		// store key in safe way

		JsonObject cipheredMsgData = new JsonObject();
		String key_s = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
		String signedKey = CryptoUtils.sign(key_s, getKp().getPrivate());

		cipheredMsgData.addProperty("sharedKeySigned", signedKey);
		cipheredMsgData.addProperty("sharedKeyNotSigned", key_s);

		byte[] cipheredData = CryptoUtils.cipherKey(gson.toJson(cipheredMsgData).getBytes(), getServerPK());

		JsonObject msgData = new JsonObject();
		msgData.addProperty("cipheredMsgData", Base64.getEncoder().encodeToString(cipheredData));
		msg.add("msgData", msgData);

		return msg;
	}

	public JsonObject generateSubmitSharedKeyTest()
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		JsonObject msg = new JsonObject();
		msg.addProperty("userId", getId());
		msg.addProperty("msgType", ClientMessageTypes.submitSharedKey.getValue());
		JsonObject cipheredMsgData = new JsonObject();

		SecretKey key = CryptoUtils.generateKeyAES();
		byte[] encoded = key.getEncoded();

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getServerPK());

		encoded = cipher.doFinal(encoded);
		String encodedKey = encoded.toString();
		String bb = Base64.getEncoder().encodeToString(encoded);
		String s = new String(encoded, "UTF-8");

		cipheredMsgData.addProperty("sharedKeyNotSigned", bb);

		JsonObject encryptedData = new JsonObject();
		encryptedData.addProperty("isTrue", true);
		IvParameterSpec iv = CryptoUtils.generateIv();

		String ivs = CryptoUtils.getIvForMessage(iv);
		cipheredMsgData.addProperty("iv", ivs);

		String encryptedString = CryptoUtils.cipherMsg(encryptedData.toString(), key, iv);

		cipheredMsgData.addProperty("encryptedString", encryptedString);
		msg.add("msgData", cipheredMsgData);
		System.out.println("[client sending AES key]:" + key.toString());
		return msg;

	}
	// ##General Procedure Methods##//

	// Broadcast Request to other users
	public void requestLocationProof() {
		String msg = generateLocationRequest();
		try {
			bltth.sendBroadcastToNearby(msg);
			System.out.println("User ID: " + this.getId() + " requested proof of location");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Handle a response to a proof previously submitted
	public void hadleResponseMessage(JsonObject msg) {
		msg.remove("senderPort");

		System.out.println("User ID:" + this.getId() + " received proof request! Sender ID:"
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
					submitLocationReport(SubmitLocationReport(rm.getAllReports(), msgId));
					System.out.println("User ID:" + getId() + " Sent Location Request With Proof Reports \n"
							+ SubmitLocationReport(rm.getAllReports(), msgId).toString() + "\n");
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 2000, TimeUnit.MILLISECONDS);
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

}
