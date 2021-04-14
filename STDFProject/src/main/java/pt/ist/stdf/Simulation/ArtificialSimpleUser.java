package pt.ist.stdf.Simulation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.User.SimpleUser;

public class ArtificialSimpleUser extends SimpleUser{
	
	private final int numEpochToSimulate;

	public ArtificialSimpleUser(String serverHost, int serverPort, GridLocation loc, Bluetooth bltth, int numEpochToSimulate) {
		super(serverHost, serverPort, loc, bltth);
		System.out.println("Init user");
		this.numEpochToSimulate = numEpochToSimulate;
	}
	
	public static int convertPosToBluetoothPort(int x, int y) {
		
		return Main.BLUETOOTH_PORT+y*Main.GRID_X+x;	
	}
	public void _move(int x, int y) { 
		((GridLocation)this.loc).setPosition(x,y);
		((BluetoothSimulation)this.bltth).changePort(convertPosToBluetoothPort(x, y));
	}
	
	public void _requestProof() {
		this.requestLocationProof();
	}
	
	private void _submitLocationToServe() {
		
	}
	
	private void _prepareEmulation() {
		
	}
	
	public void startSimulation(int type) {
	switch(type) {
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

		//listenForResponse();
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
	case 4:
		JsonObject joo = generateSubmitSharedKey();
		submitLocationReport(joo);
		System.out.println("[CLIENT] Submit shared key");

	default:
		break;
	}}
		
	public void advanceEpoch() {
		
	}

}
