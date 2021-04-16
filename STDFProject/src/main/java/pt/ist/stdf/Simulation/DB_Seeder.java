package pt.ist.stdf.Simulation;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


import pt.ist.stdf.CryptoUtils.CryptoUtils;

public class DB_Seeder {

	private static int NUM_SIMULATED_USERS=8;
	private static int NUM_EPOCHS=5;
	SimulatedUserRepository simulatedUserRepo;
	SimulatedServerRepository simulatedServerRepo;
	ClientRepository clientRepo;
	ClientEpochRepository clientEpochRepo;
	EpochRepository epochRepo;
	List<SimulatedUser> users = new ArrayList<SimulatedUser>();
	List<Client> clients = new ArrayList<Client>();
	List<Epoch> epochs = new ArrayList<Epoch>();
	
	public DB_Seeder(SimulatedUserRepository simulatedUserRepo, SimulatedServerRepository simulatedServerRepo,
			ClientRepository clientRepo, ClientEpochRepository clientEpochRepo, EpochRepository epochRepo) {
		super();
		this.simulatedUserRepo = simulatedUserRepo;
		this.simulatedServerRepo = simulatedServerRepo;
		this.clientRepo = clientRepo;
		this.clientEpochRepo = clientEpochRepo;
		this.epochRepo = epochRepo;
	}
	
	public void fillFull() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		fillSimulatedUsers();
		fillClientsWithSimulatedUsers();
		fillEpochs();
		fillClientEpochRepo();
		fillSimulatedServerRepo();
		
	}
	
	public void eraseRepos() {
		clientEpochRepo.deleteAll();
		simulatedServerRepo.deleteAll();
		simulatedUserRepo.deleteAll();
		clientRepo.deleteAll();
		epochRepo.deleteAll();
	}
	
	private void fillSimulatedUsers() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		for(int i=1;i<=NUM_SIMULATED_USERS;i++) {
			KeyPair kp = CryptoUtils.generateKeyPair();
			System.out.println("Keypair : "+kp.toString()+" "+kp.getPrivate().getEncoded().toString());
			String priv = CryptoUtils.getKeyToString(kp.getPrivate().getEncoded());
			String pub =  java.util.Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
			//String signedString = new String(pub.getBytes(), "UTF-8");

			System.out.println(priv.length());
			System.out.println(pub.length());
			//System.out.println(priv);
			//System.out.println(pub);
			//String pub ;
			SimulatedUser user = new SimulatedUser(i,priv,pub);
			user.setId(i);
			System.out.println("Creating user with id; "+user.getId());
			users.add(user);
			simulatedUserRepo.save(user);

	}
}
	private void fillClientsWithSimulatedUsers() {
		int i=0;
		for(SimulatedUser su : users) {
			i++;
			Client c = new Client(su.getPrivateKey(),su.getPublicKey());
			c.setId(i);
			clients.add(c);
			clientRepo.save(c);
		}
	}
	
	private void fillEpochs() {
		for(int i=1;i<NUM_EPOCHS+1;i++) {
			Epoch ep = new Epoch(i);
			epochs.add(ep);
			epochRepo.save(ep);
		}
	}
	
	private void fillClientEpochRepo() {
		for(Epoch ep: epochs) {
			for(Client client : clients) {
		ClientEpoch clientEpoch = new ClientEpoch();
		clientEpoch.setClient(client);
		clientEpoch.setEpoch(ep);
		clientEpoch.setX_position(4);
		clientEpoch.setY_position(4);
		clientEpochRepo.save(clientEpoch);
		}
	}}
	
	private void fillSimulatedServerRepo() throws NoSuchAlgorithmException {
		KeyPair kp = CryptoUtils.generateKeyPair();
		SimulatedServer server = new SimulatedServer();
		server.setId(1);
		String priv = java.util.Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
		String pub =  java.util.Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
		server.setPrivateKey(priv);
		server.setPublicKey(pub);
		simulatedServerRepo.save(server);

	}
	
	

}
