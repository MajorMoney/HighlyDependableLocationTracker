package pt.ist.stdf.HighlyDependableTracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import pt.ist.stdf.HighlyDependableTracker.CryptoUtils.CryptoUtils;
import pt.ist.stdf.HighlyDependableTracker.Model.SimulatedServer;
import pt.ist.stdf.HighlyDependableTracker.Model.SimulatedUser;
import pt.ist.stdf.HighlyDependableTracker.ServerProgram.Server;
import pt.ist.stdf.HighlyDependableTracker.Simulation.SimpleUserSimulation;
import pt.ist.stdf.HighlyDependableTracker.Simulation.DB_Seeder;
import pt.ist.stdf.HighlyDependableTracker.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.HighlyDependableTracker.UserProgram.Location.GridLocation;
import pt.ist.stdf.HighlyDependableTracker.data.*;

@Configuration
@SpringBootApplication(scanBasePackageClasses = { ClientRepository.class, SimulatedUserRepository.class,
		SimulatedServerRepository.class })
public class SimulationMain {

	private static final int NUM_EPOCHS = 40;

	private static final String serverHost = "localhost";
	private static final int serverPort = 8888;

	public static final int GRID_X = 2;
	public static final int GRID_Y = 2;
	private static final int BLUETOOTH_RANGE = 1;
	public static final int BLUETOOTH_PORT = 8080;
	private static final int NUM_USERS_SIMULATE = 3;
	private static final int CLUSTER_SIZE = 1;
	private SimulationMain instance;

	private int currEpoch = 1;
	@Autowired
	SimulatedUserRepository userRepository;
	@Autowired
	SimulatedServerRepository serverRepository;
	@Autowired
	ClientRepository clientRepository;
	@Autowired
	EpochRepository epochRepository;
	@Autowired
	ClientEpochRepository clientEpochRepository;

	private static ArrayList<SimpleUserSimulation> users = new ArrayList<SimpleUserSimulation>();
	private static ArrayList<Server> servers = new ArrayList<Server>();

	private ThreadPoolExecutor workers;

	private static boolean map[][];

	public SimulationMain getInstance() {
		if (instance != null)
			return instance;
		else {
			System.out.println("is null");
			instance = new SimulationMain();
			return instance;
		}
	}

	private static void startMap() {

		map = new boolean[GRID_Y][GRID_X];

		for (int y = 0; y < GRID_Y; y++) {
			for (int x = 0; x < GRID_X; x++) {
				map[y][x] = false;
			}
		}
	}

	private static int[] getNewRadomPosition() {
		int xy[] = new int[2];
		int x = (int) (Math.random() * GRID_X);
		int y = (int) (Math.random() * GRID_Y);
		while (map[x][y]) {
			x = (int) (Math.random() * GRID_X);
			y = (int) (Math.random() * GRID_Y);
		}
		map[x][y] = true;
		xy[0] = x;
		xy[1] = y;
		return xy;
	}

	private void initRandomUser(int id) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		int xy[] = getNewRadomPosition();

		int x = xy[0];
		int y = xy[1];

		GridLocation loc = new GridLocation(x, y);
		BluetoothSimulation bltth = new BluetoothSimulation(BLUETOOTH_RANGE,
				SimpleUserSimulation.convertPosToBluetoothPort(x, y), BLUETOOTH_PORT, GRID_X, GRID_Y);

		SimulatedServer ss = serverRepository.findById(1).get();
		PublicKey pubServer = CryptoUtils.getPublicKeyFromString(ss.getPublicKey());
		users.add(new SimpleUserSimulation(serverHost, serverPort, loc, bltth, NUM_EPOCHS, getUserKeys(id), pubServer,
				id));
	}

	private void intiUsers() throws NoSuchAlgorithmException, UnsupportedEncodingException {

		for (int i = 1; i <= NUM_USERS_SIMULATE; i++) {
			initRandomUser(i);
		}
	}

	private void setUpWorkers() {
		workers = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		workers.setCorePoolSize(1);
		workers.setMaximumPoolSize(NUM_USERS_SIMULATE);
	}

	private void computeSimulation() {
		for (SimpleUserSimulation user : users) {
			Runnable task = () -> {
				try {
					user.startSimulation((int) (Math.random()*3));
				} catch (InterruptedException | UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					e.printStackTrace();
				}
			};
			workers.execute(task);
		}
	}

	private void createServers() {
		for (int j = 1; j <= CLUSTER_SIZE; j++) {
			try {
				Server s = new Server(clientRepository, epochRepository, clientEpochRepository, getServerKeys(j));
				servers.add(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private KeyPair getServerKeys(int i) {
		System.out.println("id= " + i);
		SimulatedServer srver = serverRepository.findById(i).get();

		String priv = srver.getPrivateKey();
		String pub = srver.getPublicKey();

		PrivateKey privK = CryptoUtils.getPrivateKeyFromString(priv);
		PublicKey pubK = CryptoUtils.getPublicKeyFromString(pub);

		return new KeyPair(pubK, privK);
	}

	private KeyPair getUserKeys(int i) {
		SimulatedUser sim = userRepository.findById(i);

		String pubKeyString = sim.getPublicKey();
		String privKeyString = sim.getPrivateKey();

		PrivateKey priKey = CryptoUtils.getPrivateKeyFromString(privKeyString);
		PublicKey pubKey = CryptoUtils.getPublicKeyFromString(pubKeyString);
		return new KeyPair(pubKey, priKey);
	}

	private void startServers() {
		for (Server s : servers) {
			s.Start();
		}
	}

	public void setEpochTimer() {
		Timer myTimer = new Timer();
		TimerTask myTask = new TimerTask() {
			@Override
			public void run() {
				advanceEpoch();
				System.out.println("ADVANCE A NEW EPOCH TO " + currEpoch);
			}
		};
		myTimer.scheduleAtFixedRate(myTask, (20 * 1000), (20 * 1000));
	}

	public void advanceEpoch() {
		currEpoch++;
		for (SimpleUserSimulation u : users) {
			u.setEpoch(currEpoch);
		}
		for (Server s : servers) {
			s.setEpoch(currEpoch);
		}
	}

	public static void main(String args[]) {
		SpringApplication.run(SimulationMain.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {

			DB_Seeder dbs = new DB_Seeder(userRepository, serverRepository, clientRepository, clientEpochRepository,
					epochRepository);
			dbs.eraseRepos();
			dbs.fillFull();
			SimulationMain s = new SimulationMain();
			try {
				s.initRepos(serverRepository, userRepository, clientRepository, clientEpochRepository, epochRepository);
				startMap();
				s.createServers();
				s.setEpochTimer();
				s.setUpWorkers();
				s.intiUsers();
				s.computeSimulation();
				s.startServers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	private void initRepos(SimulatedServerRepository serverRepository2, SimulatedUserRepository userRepository2,
			ClientRepository clientRepository2, ClientEpochRepository clientEpochRepository2,
			EpochRepository epochRepository2) {
		this.serverRepository = serverRepository2;
		this.userRepository = userRepository2;
		this.clientRepository = clientRepository2;
		this.clientEpochRepository = clientEpochRepository2;
		this.serverRepository = serverRepository2;
	}

	public void saveUser() {
		SimulatedUser a = new SimulatedUser("priv ", "pub ");
//		userRepository.save(a);
	}

}
