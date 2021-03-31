package pt.ist.stdf.ServerProgram;

import java.net.*;
import java.util.*;
import pt.ist.stdf.ServerProgram.HandleClient.Client;

import java.io.*;

public class Server {

	public static int PORT = 8888;
	public static HashMap<Integer, Client> clients = new HashMap<Integer, Client>();
	public static ServerSocket serverSocket;
	public static Integer id;
	
	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);

	}

	public void Start() {
		try {
			while (true) {

				Socket s = serverSocket.accept();
				Client client = new Client(1, s);
				clients.put(id, client);
				client.Connect();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Server s = new Server();
		s.Start();
	}

}
