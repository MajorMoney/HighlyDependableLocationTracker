package pt.ist.stdf.UserProgram.User;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.Location;

public class SimpleUser extends User {

	private final int REQUEST_VALDATION = 0;
	private final int RESPONSE_TO_VALIDATION = 1;
	private final int REPORT_SUBMISSION = 2;
	private final int SERVER_PORT = 8888;

	protected Location loc;
	protected Bluetooth bltth;

	private SimpleUserMessageHandler messageHandler;
	private ScheduledExecutorService timer;

	private HashMap<Integer, ReportMaker> openReportMakers;
	private List<Integer> sentReports;

	private Socket serverSocket;
	private DataInputStream in;
	private DataOutputStream out;

	public SimpleUser(String serverHost, int serverPort, Location loc, Bluetooth bltth) {
		super(serverHost, serverPort);
		this.loc = loc;
		this.bltth = bltth;
		openReportMakers = new HashMap<Integer, ReportMaker>();
		sentReports = Collections.synchronizedList(new ArrayList<Integer>());
		setUpTimer();
		messageHandler = new SimpleUserMessageHandler(this, bltth);
		messageHandler.start();
		openServerConnection();// Temporariariament aqui
		System.out.println("User created at position " + loc.getCurrentLocation() + " -->ID: " + this.getId());
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
				System.out.println("Report Sended with Id: " + msgId);
			}
		} else {
			System.out.println("Strange, Report alredy sent with Id: " + msgId);
		}

	}

	private synchronized void removeSentReport(int msgId) {
		if (checkSentReports(msgId)) {
			synchronized (sentReports) {
				sentReports.remove(sentReports.indexOf(msgId));
			}
			System.out.println("Reports Handled For Id: " + msgId);
		} else {
			System.out.println("Strange, Report doesnt exist with Id: " + msgId);
		}
	}

	private synchronized boolean checkReportMaker(int msgId) {
		synchronized (openReportMakers) {
			if (openReportMakers.containsKey(msgId)) {
				return true;
			}
			return false;
		}
	}

	private synchronized void addToReportMaker(int msgId, JsonObject msg) {
		if (checkReportMaker(msgId)) {
			synchronized (openReportMakers) {
				openReportMakers.get(msgId).add(msg);
			}
			System.out.println("New report: " + msg + "\n Added with Id: " + msgId);
		}
	}

	private synchronized void addToReportMaker(int msgId, JsonObject msg, ReportMaker rm) {
		if (!checkReportMaker(msgId)) {
			synchronized (openReportMakers) {
				openReportMakers.put(msgId, rm);
				openReportMakers.get(msgId).add(msg);
			}
			System.out.println("New RM: ID " + msgId);
		}
	}

	private synchronized void removeReportMaker(int msgId) {
		if (checkReportMaker(msgId)) {
			synchronized (openReportMakers) {
				openReportMakers.remove(msgId);
			}
			System.out.println("RM removed with Id: " + msgId);
		}

	}

	// ###JSON Messages constructing functions##//

	// Generates Report to send other users
	private String generateLocationRequest() {

		JsonObject msgData = new JsonObject();
		// Perguntar ao stor se da para usar tempo (data:horas:minutos:segundos)
		msgData.addProperty("epoch", "1");
		msgData.addProperty("position", loc.getCurrentLocation());

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

		System.out.println("User ID:" + this.getId() + " received proof request from user ID:" + msg.get("userId"));
		System.out.println("MSG: " + msg.toString());

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

		System.out.println("Thread: " + Thread.currentThread() + " Handled the response");

		bltth.respondRequest(msg);

		// else
		// report to server

	}

	// Generate report message for server
	private JsonObject GenerateLocationReport(JsonArray reports, int msgId) {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "1");
		msgData.addProperty("position", loc.getCurrentLocation());
		msgData.add("reports", reports);

		JsonObject obj = new JsonObject();

		obj.addProperty("msgType", REPORT_SUBMISSION);
		obj.addProperty("userId", this.getId());
		obj.addProperty("msgId", msgId);// Se calhar não recisa
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
		
		System.out.println(
				"User ID:" + this.getId() + " received response to proof request! Sender ID:" + msg.get("userId"));
		System.out.println("MSG:" + msg.toString());

		int msgId = msg.get("msgId").getAsInt();
		if (checkSentReports(msgId)) {
			if (!checkReportMaker(msgId)) {
				ReportMaker rm = new ReportMaker(msgId);
				addToReportMaker(msgId, msg, rm);
				startNewTimer(rm, msgId);
			} else {
				addToReportMaker(msgId, msg);
			}
		}
	}

	private void startNewTimer(ReportMaker rm, int msgId) {
		timer.schedule(new Runnable() {
			public void run() {
				try {
					System.out.println("Timed Thread Executing...");
					removeSentReport(msgId);
					removeReportMaker(msgId);
					submitLocationReport(GenerateLocationReport(rm.getAllReports(), msgId));
					System.out.println(GenerateLocationReport(rm.getAllReports(), msgId).toString());
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, 2000, TimeUnit.MILLISECONDS);
	}

	private void submitLocationReport(JsonObject j) {
		try {
			out.write(j.toString().getBytes(StandardCharsets.UTF_8));
			out.flush();
			//out.close();
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
