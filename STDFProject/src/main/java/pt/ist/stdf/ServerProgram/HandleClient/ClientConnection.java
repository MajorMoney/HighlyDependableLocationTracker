package pt.ist.stdf.ServerProgram.HandleClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientConnection extends Thread{
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;
	private int id;
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;  
	
	private byte[] buffer = new byte[BUFFER_SIZE];
	private LinkedBlockingQueue<JsonObject> messages;
	
	public HashMap<Integer,Integer> locations = new HashMap<Integer,Integer>();
	
	public ClientConnection(int id, Socket socket, LinkedBlockingQueue<JsonObject> messages)
	{
		this.messages=messages;
		this.id= id;
		this.socket = socket;
		System.out.println("New CC wit ID:"+id);
	}
	
	
	
	public void Connect() {
		try {
			input=new DataInputStream(socket.getInputStream());
			output =new DataOutputStream(socket.getOutputStream());
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void run() {
		Connect();		
	}



	public void listen() {
		try {
			input.read(buffer,0,buffer.length);
			String s = new String(buffer,StandardCharsets.UTF_8);
			System.out.println("Received msg on CC "+id+": "+s);
			JsonObject msg = JsonParser.parseString(s).getAsJsonObject();
			messages.put(msg);
//			ClientMessage cm = new ClientMessage(buffer);
//			System.out.println(cm);
//			cm.PrintSubmitLocationReport();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} 
	}
	public void setPosition(int epoch, int position)
	{
		locations.put(epoch, position);
	}
	
	public Integer getPositionAtEpoch(int epoch)
	{
		return locations.get(epoch);
	}
	
	
	public boolean equals(ClientConnection clientConnection)
	{
		if(clientConnection.id == this.id)
			return true;
		return false;
	}
}
