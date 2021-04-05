package pt.ist.stdf.Simulation;

import java.io.IOException;
import java.util.ArrayList;

import pt.ist.stdf.ServerProgram.Server;
import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.User.SimpleUser;

public class Main {

	// Mudar alguns argumentos para serem recebidos no command line
	private static final int NUM_EPOCHS = 40;

	private static final String serverHost = "localhost";
	private static final int serverPort = 8888;

	public static final int GRID_X = 2;
	public static final int GRID_Y = 2;
	private static final int BLUETOOTH_RANGE = 1;
	public static final int BLUETOOTH_PORT = 8080;
	private static final int NUM_USERS_SIMULATE = 20;

	private static ArrayList<ArtificialSimpleUser> users = new ArrayList<ArtificialSimpleUser>();

	private static boolean map[][];

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

	private static void initRandomUser() {
		int xy[] = getNewRadomPosition();
		int x = xy[0];
		int y = xy[1];

		GridLocation loc = new GridLocation(x, y);
		BluetoothSimulation bltth = new BluetoothSimulation(BLUETOOTH_RANGE,
				ArtificialSimpleUser.convertPosToBluetoothPort(x, y), BLUETOOTH_PORT, GRID_X, GRID_Y);

		users.add(new ArtificialSimpleUser(serverHost, serverPort, loc, bltth, NUM_EPOCHS));

	}

	private static void intiUsers() {

		for (int i = 0; i < NUM_USERS_SIMULATE; i++) {
			initRandomUser();
		}
	}

	private static void computeSimulation() {
		for (ArtificialSimpleUser user : users) {
			user.startSimulation();
		}
	}

	// Correr com args[0]=1 para executar proof request
	// args[0]=0 para criar users que só vão dar listen
	public static void main(String args[]) {

		if (args[0].equals("2")) {
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
		}

		// Fazer MultiThread
		// intiUsers();
		// computeSimulation();

//		for(int e=1; e<= NUM_EPOCHS; e++) {
//			for(ArtificialSimpleUser user: users) {
//				user.advanceEpoch();
//			}
//		}

	}

}
