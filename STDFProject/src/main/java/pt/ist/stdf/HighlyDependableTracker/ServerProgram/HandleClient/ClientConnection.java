package pt.ist.stdf.HighlyDependableTracker.ServerProgram.HandleClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.ist.stdf.HighlyDependableTracker.ServerProgram.Server;

public class ClientConnection extends Thread{
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;
	private int id;
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;  
	private Server server;
	
	private byte[] buffer = new byte[BUFFER_SIZE];
	private LinkedBlockingQueue<JsonObject> messages;
	
	public HashMap<Integer,Integer> locations = new HashMap<Integer,Integer>();
	
	public ClientConnection(int id, Socket socket, LinkedBlockingQueue<JsonObject> messages,Server server)
	{
		this.messages=messages;
		this.id= id;
		this.socket = socket;
		System.out.println("New CC wit ID:"+id);
		this.server = server;
	}
	
	
	
	public void Connect() throws Exception {
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
		try {
			Connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}



	public void listen() throws Exception {
		try {
			input.read(buffer,0,buffer.length);
			String s = new String(buffer,StandardCharsets.UTF_8);
			System.out.println("Received msg on CC "+id+": "+s);
			
			JsonObject msg = JsonParser.parseString(s.trim()).getAsJsonObject();
			messages.put(msg);
			ClientMessage cm = new ClientMessage(buffer,server,this);
//			System.out.println(cm);
			/**cm.PrintSubmitLocationReport();
			id = cm.getUserId();
			Position position = cm.getPosition();
			int epoch = cm.getEpoch();
			System.out.println("Id is now set to "+id+ " and epoch is "+epoch + " and position is "+position);
			ClientEpoch clientEpoch = new ClientEpoch();
			clientEpoch.setEpoch(server.findEpochById(epoch));
			clientEpoch.setClient(server.findClientById(id));
			clientEpoch.setX_position(position.x);
			clientEpoch.setY_position(position.y);
			
			System.out.println("ClientEpoch print: "+clientEpoch.getPrimaryKey().getClient().getId() +"   -> "+clientEpoch.getPrimaryKey().getEpoch().getId());
			**/
			/**Client c = server.findClientById(id);
			if(c!=null)
				System.out.println("IS NULL -----------------------------------------");
			clientEpoch.setClient(server.findClientById(id));
			ClientEpochId epochId = new ClientEpochId();
			epochId.setClient(server.findClientById(id));
			epochId.setEpoch(server.findEpochById(epoch));
			//clientEpoch.setPrimaryKey(epochId);
			**/
			//server.addClientEpoch(clientEpoch);
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
	
	public void send(JsonObject j) {
		try {
			DataOutputStream dout =new DataOutputStream (socket.getOutputStream());
			dout.write(j.toString().getBytes(StandardCharsets.UTF_8));
			dout.flush();
			//dout.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
