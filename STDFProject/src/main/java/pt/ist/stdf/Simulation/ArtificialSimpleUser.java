package pt.ist.stdf.Simulation;

import java.security.KeyPair;
import java.security.PublicKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.User.SimpleUser;

public class ArtificialSimpleUser extends SimpleUser {

	private final int numEpochToSimulate;

	public ArtificialSimpleUser(String serverHost, int serverPort, GridLocation loc, Bluetooth bltth,
			int numEpochToSimulate, KeyPair kp,PublicKey serverPK) {
		super(serverHost, serverPort, loc, bltth, kp,serverPK);
		System.out.println("Init user");
		this.numEpochToSimulate = numEpochToSimulate;
	}

	public static int convertPosToBluetoothPort(int x, int y) {

		return Main.BLUETOOTH_PORT + y * Main.GRID_X + x;
	}

	public void _move(int x, int y) {
		((GridLocation) this.loc).setPosition(x, y);
		((BluetoothSimulation) this.bltth).changePort(convertPosToBluetoothPort(x, y));
	}

	public void _requestProof() {
		this.requestLocationProof();
	}

	private void _submitLocationToServe() {

	}

	private void _prepareEmulation() {

	}

	public void startSimulation(int type) throws InterruptedException {
		switch (type) {
		case 0:
			JsonArray rep0 = generateTestReports();
			JsonObject jj = generateSubmitLocationReport(rep0, 3);
			submitLocationReport(jj);
			System.out.println("[CLIENT] Submitted location report");
			break;
		case 1:
			JsonObject ja = generateObtainLocationReport();
			submitLocationReport(ja);
			System.out.println("[CLIENT] Obtain location report");

			// listenForResponse();
			break;
		case 2:
			JsonObject je = generateObtainLocationReportHA();
			submitLocationReport(je);
			System.out.println("[HA CLIENT] Obtain location report");
			break;
		case 3:
			JsonObject jo = generateObtainUsersAtLocationHA();
			submitLocationReport(jo);
			System.out.println("[HA CLIENT] Obtain users @ location");
			break;
		case 4:
			JsonObject joo = generateSubmitSharedKey();
			submitLocationReport(joo);
			System.out.println("[CLIENT] Submit shared key");
			break;
		case 5:
			System.out.println("[CLIENT " + getId() + "] LocationRequest");
			_requestProof();
			break;

		default:
			break;
		}
	}

	public void advanceEpoch() {

	}

}
