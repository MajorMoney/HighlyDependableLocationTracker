package pt.ist.stdf.ServerProgram;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ist.stdf.ServerProgram.HandleClient.ClientConnection;

public class ServerGrid {

	private static final int MAX_GRID_SIZE = 100;
	public HashMap<Position,ArrayList<ClientConnection>> locations = new HashMap<Position,ArrayList<ClientConnection>>();
	public static int epoch;
	public ServerGrid() {
		
			for(int x=0;x<MAX_GRID_SIZE;x++ ) 
				for(int y=0;y<MAX_GRID_SIZE;y++) {
					Position coord = new Position(x,y);
					locations.put(coord,null);
	}
	
}
	public void AddClientToGrid (Position position,ClientConnection clientConnection)
	{
		locations.get(position).add(clientConnection);
	}
	
	public Position getClientPosition(ClientConnection clientConnection)
	{
		for (Position pos : locations.keySet()) {
			if(locations.get(pos).contains(clientConnection))
			{
				return pos;
			}
		}
		return null;
		
	}

}
