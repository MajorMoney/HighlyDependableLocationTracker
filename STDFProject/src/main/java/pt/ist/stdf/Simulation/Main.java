package pt.ist.stdf.Simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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

import pt.ist.stdf.ServerProgram.Server;
import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;





@SpringBootApplication(scanBasePackages= {"pt.ist.stdf.ServerProgram.database","pt.ist.stdf.Simulation"})
@ComponentScan(basePackages = {"pt.ist.stdf.ServerProgram.database"})
@EnableAutoConfiguration
public class Main {

	// Mudar alguns argumentos para serem recebidos no command line
	private static final int NUM_EPOCHS = 40;

	private static final String serverHost = "localhost";
	private static final int serverPort = 8888;

	public static final int GRID_X = 50;
	public static final int GRID_Y = 50;
	private static final int BLUETOOTH_RANGE = 1;
	public static final int BLUETOOTH_PORT = 8080;
	private static final int NUM_USERS_SIMULATE = 2;
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

	
	private static boolean map[][];
@PostConstruct
public void setRepo() {
	// TODO Auto-generated constructor stub
	System.out.println("---------------------------------------");
	System.out.println(userRepository.findAll());
	System.out.println("---------------------------------------");

}
	public Main getInstance(){
	if(instance!=null)
		return instance;
	else { System.out.println("is null");
	instance = new Main();
	return instance;}
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

	private void initRandomUser() {
		System.out.println("Innited rand1111 user");

		int xy[] = getNewRadomPosition();
		System.out.println("Innited rand1111 222user");

		int x = xy[0];
		int y = xy[1];
		System.out.println("Innited rand1111 user");

		GridLocation loc = new GridLocation(x, y);
		BluetoothSimulation bltth = new BluetoothSimulation(BLUETOOTH_RANGE,
				ArtificialSimpleUser.convertPosToBluetoothPort(x, y), BLUETOOTH_PORT, GRID_X, GRID_Y);

		users.add(new ArtificialSimpleUser(serverHost, serverPort, loc, bltth, NUM_EPOCHS));
		System.out.println("Innited rand user");
	}

	private void intiUsers() {

		
		for (int i = 0; i < NUM_USERS_SIMULATE; i++) {
			initRandomUser();
			saveUser();
			System.out.println("saved usser");
		}
		System.out.println("Exit");
	}

	private static void computeSimulation() {
		for (ArtificialSimpleUser user : users) {
			user.startSimulation();
		}
	}

	// Correr com args[0]=1 para executar proof request
	// args[0]=0 para criar users que só vão dar listen
	public static void main(String args[]) {		
		Main m = new Main();
		m.test(args);
		
		/**if (args[0].equals("2")) {
			try {
				Server s = new Server();
				s.Start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			startMap();

			// CUIDADO QUE ISTO ASSIM É UMA 1 THREAD A CORRER VARIOS USERS
			initRandomUser();
			if (args[0].equals("1")) {
				users.get(0)._requestProof();
			}
		}**/
	}

		public void test(String[] args1) {
		
			//
			//if (args1[0].equals("2")) {
				try {
					//intiUsers();
					
					SpringApplication.run(Main.class, args1);	
					System.out.println("Running");
				} catch (Exception e) {
					e.printStackTrace();
				//}

			} 
			
				

				// CUIDADO QUE ISTO ASSIM É UMA 1 THREAD A CORRER VARIOS USERS
				//initRandomUser();
				//if (args1[0].equals("1")) {
					//users.get(0)._requestProof();
				
			}
		// Fazer MultiThread
		// intiUsers();
		// computeSimulation();

//		for(int e=1; e<= NUM_EPOCHS; e++) {
//			for(ArtificialSimpleUser user: users) {
//				user.advanceEpoch();
//			}
//		}
		public void saveUser() {
			SimulatedUser a = new SimulatedUser("priv ","pub ");
			userRepository.save(a);
		}
		
		@Bean
		CommandLineRunner runner() {
			return args -> {
					
				Random rand = new Random(); 
				Epoch epoch = new Epoch(rand.nextInt(10000));
				Client client = new Client("teste3","teste3");
				ClientEpochId id = new ClientEpochId();
				id.setClient(client);
				id.setEpoch(epoch);
				ClientEpoch c = new ClientEpoch();
				c.setPrimaryKey(id);
				clientRepository.save(client);
				epochRepository.save(epoch);
				clientEpochRepository.save(c);
				Server s = new Server(clientRepository,epochRepository,clientEpochRepository);
				serverRepository.save(new SimulatedServer("private_key_server","public_key"));
				
				startMap();
				intiUsers();
				computeSimulation();
				System.out.println("B4 START -------------------------------------------------");

				s.Start();
				System.out.println("AFTER START -------------------------------------------------");


				userRepository.save(new SimulatedUser(" aaa ","vvv"));
			};
		}
	}

