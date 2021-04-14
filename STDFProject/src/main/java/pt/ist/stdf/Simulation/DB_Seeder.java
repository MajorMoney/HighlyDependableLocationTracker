package pt.ist.stdf.Simulation;

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
	
	
	private void fillSimulatedUsers() throws NoSuchAlgorithmException {
		for(int i=1;i<=NUM_SIMULATED_USERS;i++) {
			KeyPair kp = CryptoUtils.generateKeyPair();
			SimulatedUser user = new SimulatedUser();
			user.setPrivateKey(kp.getPrivate().toString());
			user.setPublicKey(kp.getPublic().toString());
			users.add(user);
	}
		simulatedUserRepo.saveAll(users);
}
	private void fillClientsWithSimulatedUsers() {
		for(SimulatedUser su : users) {
			Client c = new Client(su.getPrivateKey(),su.getPublicKey());
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
		clientEpoch.setClient(null);
		}
	}
	
	
	}}
