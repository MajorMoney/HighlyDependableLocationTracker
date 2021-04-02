package pt.ist.stdf.UserProgram.User;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.Location;

public class SimpleUser extends User{
	
	private final int REQUEST_VALDATION = 0;
	private final int RESPONSE_TO_VALIDATION = 1;
	
	protected Location loc;
	protected Bluetooth bltth;
	
	private SimpleUserMessageHandler messageHandler;
	private boolean proof_requested;
	
	
	public SimpleUser(String serverHost, int serverPort, Location loc, Bluetooth bltth) {
		super(serverHost, serverPort);
		this.loc = loc;
		this.bltth = bltth;
		//FALTA IMPLEMENTAR
		//QUANDO MUDAR PARA TRUE COMEÇAR UM TIMER E NO FIM DO TIMER MUDAR PARA FALSO 
		this.proof_requested=false;
		messageHandler = new SimpleUserMessageHandler(this, bltth);
		messageHandler.start();
		System.out.println("User created at position "+loc.getCurrentLocation()+" -->ID: " +this.getId());
	}

	//Generates Report to send other users
	private String generateLocationReport()
	{
		
		JsonObject msgData = new JsonObject();
		//Perguntar ao stor se da para usar tempo (data:horas:minutos:segundos)
		msgData.addProperty("epoch", "1");
		msgData.addProperty("position", loc.getCurrentLocation());
		
		JsonObject obj = new JsonObject();
		obj.addProperty("msgType", REQUEST_VALDATION);
		obj.addProperty("userId", this.getId());
		obj.add("msgData", msgData);
		return obj.toString();
	}
	
	//Broadcast Report to other users
	public void requestLocationProof() {
		String msg = generateLocationReport();
			try {
				System.out.println("User ID: "+this.getId()+" requested proof of identification");
				bltth.sendBroadcastToNearby(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	//Responds to a proof request 
	public void respondLocationProof(JsonObject msg) {
		
		System.out.println("User ID:"+ this.getId() + " received proof request from user ID:" + msg.get("userId"));
		
		
		//if position valid 
			//respond:
		
		//Change message body and send response
		msg.addProperty("msgType", RESPONSE_TO_VALIDATION);
		msg.addProperty("userId", Integer.toString(this.getId()));
		
		JsonObject msgData = new JsonObject();
		
		StringBuilder singature = new StringBuilder();
		singature.append("Assinado por: ");
		singature.append(this.getId());
		msgData.addProperty("singature,", singature.toString());
		
		msg.add("msgData", msgData);
		
		bltth.respondRequest(msg);
		
		
		//else 
			//report to server
		
		

	}
	
	//Handler a response to a proof previously submitted
	public void hadleResponseMessage(JsonObject msg) {
		
		//if proof_requested 
			//accept and send server
		//report to server if someon tries to forge challange
			//proof_requested = false
		//else 
			//ignore
		
		System.out.println("User ID:"+ this.getId() + " received response to proof requested! Sender ID:" + msg.get("userId"));
	}
	
	
	
	//Submits proof to server
	public void submitLocationReport() {
		JsonArray position = new JsonArray();
		position.add(1);
		position.add(2);
		JsonArray reports = new JsonArray();
		String rep1= Base64.getEncoder().encodeToString(generateLocationReport().toString().getBytes(StandardCharsets.UTF_8));
		String rep2= Base64.getEncoder().encodeToString(generateLocationReport().toString().getBytes(StandardCharsets.UTF_8));
		reports.add(rep1);
		reports.add(rep2);
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "1");
		msgData.add("position", position);
		msgData.addProperty("num_reports", "2");
		msgData.add("reports", reports);

		JsonObject obj = new JsonObject();
		obj.addProperty("userId", "3");
		obj.addProperty("msgType", "0");
		obj.add("msgData", msgData);

		System.out.println("[CLIENT] length: "+obj.toString().getBytes().length);
		
		//Send to server
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public void obtaionLocationReport() {
//		
//	}
//	
//	//Classe a chamar no main para simular o user
//	public void testSomething() {
//		
//		try {
//			Thread.sleep(3000);
//				bltth.sendBroadcastToNearby("ola");		
//		} catch (InterruptedException e2) {	
//			e2.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	//Classe a chamar no main para simular o user
//	public void testSomethingWithServer() {
//		
//		try {
//			Socket s=new Socket("localhost",8888);  
//			DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
//			JsonObject j = submitLocationRport();
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
