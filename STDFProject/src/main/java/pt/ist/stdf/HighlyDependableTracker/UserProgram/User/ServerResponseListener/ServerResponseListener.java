package pt.ist.stdf.HighlyDependableTracker.UserProgram.User.ServerResponseListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerResponseListener extends Thread{
	private static final int MAX_UDP_DATA_SIZE = (64 * 1024 - 1) - 8 - 20;
	private static final int BUFFER_SIZE = MAX_UDP_DATA_SIZE;

	
	private LinkedBlockingQueue<JsonObject> messages;
	private DataInputStream inStream;
	private Socket socket;
	
	public ServerResponseListener(Socket socket, LinkedBlockingQueue<JsonObject> messages) throws IOException {
		this.socket=socket;
		inStream = new DataInputStream(this.socket.getInputStream());;
		this.messages = messages;
	}
	public void setMessages(LinkedBlockingQueue<JsonObject> messages) {
		this.messages = messages;
	}
	
	/**
	public synchronized void changeSocket() {
        this.socket = socket;
        notify();
        if (socket==null) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	**/
	
	@Override
	public void run() {
		System.out.println("STARTING");
		byte[] buf = new byte[BUFFER_SIZE];
		String s="";
		while(true) {
		try {	
			inStream.read(buf,0,buf.length);
			s = new String(buf,StandardCharsets.UTF_8);
			System.out.println("\n\n\n###ERROR ON MESSAGE###\n"+s+"\n\n\n");
			JsonObject msg = JsonParser.parseString(s.trim()).getAsJsonObject();
			messages.put(msg);
		} catch (IOException e) {	
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("\n\n\n###ERROR ON MESSAGE###\n"+s+"\n\n\n");
			e.printStackTrace();
		}}
		
	}
}
