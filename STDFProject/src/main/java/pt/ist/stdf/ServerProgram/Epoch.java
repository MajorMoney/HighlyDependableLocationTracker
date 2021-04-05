package pt.ist.stdf.ServerProgram;

import pt.ist.stdf.ServerProgram.HandleClient.ClientConnection;

public class Epoch {

	public int id;
	public ServerGrid grid;
	
	
	public Epoch(int id) {
		grid = new ServerGrid();
	}
	public Position getClientPosition(ClientConnection clientConnection)
	{
		Position pos = grid.getClientPosition(clientConnection);
		return pos;
		
	}
	public void AddClient(Position position, ClientConnection clientConnection)
	{
		grid.AddClientToGrid(position, clientConnection);
	}

}
