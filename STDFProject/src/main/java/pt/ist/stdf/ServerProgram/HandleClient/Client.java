package pt.ist.stdf.ServerProgram.HandleClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Client {
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;
	public int id;
	public Socket socket;
	public DataInputStream input;
	DataOutputStream output;  
	private byte[] buffer = new byte[BUFFER_SIZE];
	
	public HashMap<Integer,Integer> locations = new HashMap<Integer,Integer>();
	
	public Client(int id, Socket socket)
	{
		this.id= id;
		this.socket = socket;
	}
	public void Connect() {
		try {
			input=new DataInputStream(socket.getInputStream());
			output =new DataOutputStream(socket.getOutputStream());
			listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void listen() {
		try {
			input.read(buffer,0,buffer.length);
			ClientMessage cm = new ClientMessage(buffer);
			cm.PrintSubmitLocationReport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	
	public boolean equals(Client client)
	{
		if(client.id == this.id)
			return true;
		return false;
	}
}
