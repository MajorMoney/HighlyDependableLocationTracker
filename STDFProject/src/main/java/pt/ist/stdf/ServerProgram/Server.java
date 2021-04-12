package pt.ist.stdf.ServerProgram;

import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;

import pt.ist.stdf.ServerProgram.HandleClient.ClientConnection;

import java.io.*;

public class Server {
 
	public static int PORT = 8888;
	public static HashMap<Integer, ClientConnection> clientConnections = new HashMap<Integer, ClientConnection>();
	public static ServerSocket serverSocket;
	public static Integer id;
	
	private LinkedBlockingQueue<JsonObject> messages;
	
	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);
		messages= new LinkedBlockingQueue<JsonObject>() ;
	}

	public void Start() {
		try {
			int i=0;//Só para incrementar ids das cc, depois muda-se
			while (true) {
				Socket s = serverSocket.accept();
				ClientConnection clientConnection = new ClientConnection(i, s,messages);
				i++;
				clientConnections.put(id, clientConnection);
				clientConnection.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.Start();
	}

}
