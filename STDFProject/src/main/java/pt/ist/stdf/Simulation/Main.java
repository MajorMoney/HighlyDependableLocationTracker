package pt.ist.stdf.Simulation;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import pt.ist.stdf.CryptoUtils.CryptoUtils;
import pt.ist.stdf.ServerProgram.Server;
import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;

@SpringBootApplication(scanBasePackages = { "pt.ist.stdf.ServerProgram.database", "pt.ist.stdf.Simulation" })
@ComponentScan(basePackages = { "pt.ist.stdf.ServerProgram.database" })
@EnableAutoConfiguration
public class Main {

	// Mudar alguns argumentos para serem recebidos no command line
	private static final int NUM_EPOCHS = 40;

	private static final String serverHost = "localhost";
	private static final int serverPort = 8888;

	public static final int GRID_X = 2;
	public static final int GRID_Y = 2;
	private static final int BLUETOOTH_RANGE = 1;
	public static final int BLUETOOTH_PORT = 8080;
	private static final int NUM_USERS_SIMULATE = 3;
	private Main instance;

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

	private static ArrayList<ArtificialSimpleUser> users = new ArrayList<ArtificialSimpleUser>();

	private ThreadPoolExecutor workers;

	private static boolean map[][];

	public Main getInstance() {
		if (instance != null)
			return instance;
		else {
			System.out.println("is null");
			instance = new Main();
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
		// usar Set para obter nova posição
		while (map[x][y]) {
			x = (int) (Math.random() * GRID_X);
			y = (int) (Math.random() * GRID_Y);
		}
		map[x][y] = true;
		xy[0] = x;
		xy[1] = y;
		return xy;
	}

	private void initRandomUser() throws NoSuchAlgorithmException {

		int xy[] = getNewRadomPosition();

		int x = xy[0];
		int y = xy[1];

		GridLocation loc = new GridLocation(x, y);
		BluetoothSimulation bltth = new BluetoothSimulation(BLUETOOTH_RANGE,
				ArtificialSimpleUser.convertPosToBluetoothPort(x, y), BLUETOOTH_PORT, GRID_X, GRID_Y);

		KeyPair kp = CryptoUtils.generateKeyPair();

		users.add(new ArtificialSimpleUser(serverHost, serverPort, loc, bltth, NUM_EPOCHS, kp));
		System.out.println("Innited rand user");
	}

	private void intiUsers() throws NoSuchAlgorithmException {

		for (int i = 0; i < NUM_USERS_SIMULATE; i++) {
			initRandomUser();
			saveUser();
			System.out.println("saved usser");
		}
		System.out.println("Exit");
	}

	private void computeSimulation() {
		for (ArtificialSimpleUser user : users) {
			Runnable task = () -> {
				try {
					user.startSimulation(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
			workers.execute(task);
		}
	}

	public static void main(String args[]) {
		Main m = new Main();
		m.test(args);

	}

	private void setUpWorkers() {
		workers = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		workers.setCorePoolSize(1);
		workers.setMaximumPoolSize(NUM_USERS_SIMULATE);
	}

	public void test(String[] args1) {
		try {

			SpringApplication.run(Main.class, args1);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void saveUser() {
		SimulatedUser a = new SimulatedUser("priv ", "pub ");
		userRepository.save(a);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {

			Random rand = new Random();
			Epoch epoch = new Epoch(rand.nextInt(10000));
			Client client = new Client("teste3", "teste3");
			ClientEpochId id = new ClientEpochId();
			id.setClient(client);
			id.setEpoch(epoch);
			ClientEpoch c = new ClientEpoch();
			c.setPrimaryKey(id);
			clientRepository.save(client);
			epochRepository.save(epoch);
			clientEpochRepository.save(c);
			Server s = new Server(clientRepository, epochRepository, clientEpochRepository);
			serverRepository.save(new SimulatedServer("private_key_server", "public_key"));

			startMap();
			setUpWorkers();
			intiUsers();
			computeSimulation();
			System.out.println("B4 START -------------------------------------------------");

			s.Start();

			userRepository.save(new SimulatedUser(" aaa ", "vvv"));
		};
	}
}
