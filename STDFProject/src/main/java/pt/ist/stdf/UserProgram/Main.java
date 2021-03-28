package pt.ist.stdf.UserProgram;

import java.awt.Point;
import java.net.InetAddress;
import java.net.UnknownHostException;

import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.User.User;
import pt.ist.stdf.UserProgram.User.SimpleUser;

public class Main {
	
	private static final String SIMPLE_USER = "S";
	private static final String HR_USER = "HR";
	
	private static final int GRID_X = 100;
	private static final int GRID_Y = 100;
	private static final int BLUETOOTH_RANGE = 3;
	private static final int BLUETOOTH_PORT = 696969; 
		
	private static GridLocation initPosition() {
		
		int x = (int) (Math.random()*GRID_X);
		int y = (int) (Math.random()*GRID_Y);
		
		return new GridLocation(x,y);
	}
	
	private static int convertPosToBluetoothPort(GridLocation loc) {
		
		return BLUETOOTH_PORT+loc.getY()*GRID_X+loc.getX();
				
	}
	
	private static SimpleUser initSimpleUser(int serverPort, String serverHost) {
		
		User user;
		GridLocation loc = initPosition();
		BluetoothSimulation bltth = new BluetoothSimulation(BLUETOOTH_RANGE, convertPosToBluetoothPort(loc));
		
		return new SimpleUser(serverHost, serverPort, loc, bltth);
	}
	
	
	
	
	
	
	
	/*2 types of users: Simple, HR
	 *agr[0] client_type: "S", "HR"
	 *
	 *					IF SIMPLE
	 *agr[1] server_port
	 *agr[2] server_address
	 *agr[3] init_location_x
	 *agr[4] init_location_y
	 *arg[5] some sort of authorization/autheticationn
	 *
	 *
	 *					IF HR
	 *agr[1] server_port
	 *agr[2] server_address
	 *agr[3] some sort of authorization/authentication
	 */
    public static void main( String[] args ){ 
    	
		final String serverHost = args[1];
		final int serverPort = Integer.parseInt(args[1]);
			
	    if(args[0].equals(SIMPLE_USER)) {
	    	SimpleUser user;

	    	user = initSimpleUser(serverPort, serverHost);
	    		
	    	//emulate user movement and other actions
	    		
	    }else if(args[0].equals(HR_USER)) {
	    
	    	
	    	
	    }else {
	    	System.err.println("Type of user undefined.%nPlease choose valid user type");
	    }
	    	

    }

}
