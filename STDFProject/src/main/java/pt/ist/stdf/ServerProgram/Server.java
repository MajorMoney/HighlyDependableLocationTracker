package pt.ist.stdf.ServerProgram;

import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import pt.ist.stdf.ServerProgram.HandleClient.ClientConnection;
import pt.ist.stdf.Simulation.Client;
import pt.ist.stdf.Simulation.ClientEpoch;
import pt.ist.stdf.Simulation.ClientEpochRepository;
import pt.ist.stdf.Simulation.ClientRepository;
import pt.ist.stdf.Simulation.Epoch;
import pt.ist.stdf.Simulation.EpochRepository;

import java.io.*;

@Service
public class Server {
 
	public static int PORT = 8888;
	public static HashMap<Integer, ClientConnection> clientConnections = new HashMap<Integer, ClientConnection>();
	public static ServerSocket serverSocket;
	public static Integer id;
	
	private LinkedBlockingQueue<JsonObject> messages;
	
	@Autowired
	EpochRepository epochRepository;
	@Autowired
	ClientRepository clientRepository;
	@Autowired
	ClientEpochRepository clientEpochRepository;
	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);
		messages= new LinkedBlockingQueue<JsonObject>() ;
	}

	public Server(ClientRepository clientRepo, EpochRepository epochRepo,ClientEpochRepository clientEpochRepo) throws IOException {
		this.epochRepository=epochRepo;
		this.clientEpochRepository = clientEpochRepo;
		this.clientRepository=clientRepo;
		serverSocket = new ServerSocket(PORT);
		messages= new LinkedBlockingQueue<JsonObject>() ;
	}
	public void Start() {
		try {
			int i=0;//Só para incrementar ids das cc, depois muda-se
			while (true) {

				Socket s = serverSocket.accept();
				ClientConnection clientConnection = new ClientConnection(i, s,messages,this);
				
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

	public Client findClientById(int id) {
		return clientRepository.findById(id);
	}
	
	public Epoch findEpochById(int id) {
		return epochRepository.findById(id);
	}
	
	public void addClientEpoch(ClientEpoch clientEpoch) {
		clientEpochRepository.save(clientEpoch);
	}
}
