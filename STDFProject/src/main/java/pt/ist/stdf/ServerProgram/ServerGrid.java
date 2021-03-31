package pt.ist.stdf.ServerProgram;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ist.stdf.ServerProgram.HandleClient.Client;

public class ServerGrid {

	private static final int MAX_GRID_SIZE = 100;
	public HashMap<Position,ArrayList<Client>> locations = new HashMap<Position,ArrayList<Client>>();
	public static int epoch;
	public ServerGrid() {
		
			for(int x=0;x<MAX_GRID_SIZE;x++ ) 
				for(int y=0;y<MAX_GRID_SIZE;y++) {
					Position coord = new Position(x,y);
					locations.put(coord,null);
	}
	
}
	public void AddClientToGrid (Position position,Client client)
	{
		locations.get(position).add(client);
	}
	
	public Position getClientPosition(Client client)
	{
		for (Position pos : locations.keySet()) {
			if(locations.get(pos).contains(client))
			{
				return pos;
			}
		}
		return null;
		
	}

}
