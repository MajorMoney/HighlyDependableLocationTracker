package pt.ist.stdf.ServerProgram;

import pt.ist.stdf.ServerProgram.HandleClient.Client;

public class Epoch {

	public int id;
	public ServerGrid grid;
	
	
	public Epoch(int id) {
		grid = new ServerGrid();
	}
	public Position getClientPosition(Client client)
	{
		Position pos = grid.getClientPosition(client);
		return pos;
		
	}
	public void AddClient(Position position, Client client)
	{
		grid.AddClientToGrid(position, client);
	}

}
