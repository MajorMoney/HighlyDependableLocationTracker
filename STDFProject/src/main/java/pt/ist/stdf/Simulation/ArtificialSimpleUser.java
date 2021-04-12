package pt.ist.stdf.Simulation;

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
	
	public void startSimulation() {
		
	}
	
	public void advanceEpoch() {
		
	}

}
