package pt.ist.stdf.UserProgram.User;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.Location;

public class SimpleUser extends User{
	
	private Location loc;
	private Bluetooth bltth;
	
	
	public SimpleUser(String serverHost, int serverPort, Location loc, Bluetooth bltth) {
		super(serverHost, serverPort);
		this.loc = loc;
		this.bltth = bltth;
	}
	
	public JsonObject requestLocationProof() {
			return null;
	}
	
	public JsonObject submitLocationRport() {
		JsonArray position = new JsonArray();
		position.add(1);
		position.add(2);
		JsonArray reports = new JsonArray();
		//reports.
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
	return obj;

	}
	public JsonObject generateLocationReport()
	{
		JsonArray position = new JsonArray();
		position.add(3);
		position.add(4);
		
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", "1");
		msgData.add("position", position);
		
		JsonObject obj = new JsonObject();
		obj.addProperty("userId", "5");
		obj.addProperty("msgType", "3");
		obj.add("msgData", msgData);
		return obj;
	}
	public void obtaionLocationReport() {
		
	}
	
	//Classe a chamar no main para simular o user
	public void testSomething() {
		
		try {
			Thread.sleep(3000);
				bltth.sendBroadcastToNearby();		
		} catch (InterruptedException e2) {	
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Classe a chamar no main para simular o user
	public void testSomethingWithServer() {
		
		try {
			Socket s=new Socket("localhost",8888);  
			DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
			JsonObject j = submitLocationRport();

			System.out.println(j.toString());
			
			dout.write(j.toString().getBytes(StandardCharsets.UTF_8));
			dout.flush();  
			dout.close();  
			s.close(); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
