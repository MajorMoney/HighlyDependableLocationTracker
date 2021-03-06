package pt.ist.stdf.HighlyDependableTracker.ServerProgram;

import java.net.*;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import pt.ist.stdf.HighlyDependableTracker.ServerProgram.HandleClient.ClientConnection;
import pt.ist.stdf.HighlyDependableTracker.Model.Client;
import pt.ist.stdf.HighlyDependableTracker.Model.ClientEpoch;
import pt.ist.stdf.HighlyDependableTracker.Model.ClientEpochId;
import pt.ist.stdf.HighlyDependableTracker.data.ClientEpochRepository;
import pt.ist.stdf.HighlyDependableTracker.data.ClientRepository;
import pt.ist.stdf.HighlyDependableTracker.Model.Epoch;
import pt.ist.stdf.HighlyDependableTracker.data.EpochRepository;

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
	int epoch;
	private KeyPair keypair;
	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);
		messages= new LinkedBlockingQueue<JsonObject>() ;
	}

	public Server(ClientRepository clientRepo, EpochRepository epochRepo,ClientEpochRepository clientEpochRepo,KeyPair kp) throws IOException {
		this.epochRepository=epochRepo;
		this.clientEpochRepository = clientEpochRepo;
		this.clientRepository=clientRepo;
		serverSocket = new ServerSocket(PORT);
		messages= new LinkedBlockingQueue<JsonObject>() ;
		this.keypair=kp;
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

	
	public KeyPair getKeypair() {
		return keypair;
	}

	public void setKeypair(KeyPair keypair) {
		this.keypair = keypair;
	}

	public int getEpoch() {
		return epoch;
	}

	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}
	

	public List<ClientEpoch> getAllClientEpochs(){
		return clientEpochRepository.findAll();
	}
	public Optional<Client> findClientById(int id) {
		return clientRepository.findById(Integer.valueOf(id));
	}
	
	public Epoch findEpochById(int id) {
		return epochRepository.findById(id);
	}
	
	public void addClientEpoch(ClientEpoch clientEpoch) {
		clientEpochRepository.save(clientEpoch);
	}
	public void addClient(Client client) {
		clientRepository.saveAndFlush(client);
	}
	public void updateClientSharedKey(Client client)
	{
		clientRepository.setClientSharedKeyById(client.getId(),client.getSharedKey());
	}
	public Optional<ClientEpoch> findClientEpochByInt(Client c, Epoch e) {
	
		ClientEpochId id = new ClientEpochId();
		id.setClient(c);
		id.setEpoch(e);
		Optional<ClientEpoch> cc = clientEpochRepository.findById(id);
		
		System.out.println("Find client epoch by id: "+cc.toString());
		return cc;
	}
}
