package pt.ist.stdf.ServerProgram.database;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import pt.ist.stdf.ServerProgram.Position;
import pt.ist.stdf.ServerProgram.ServerGrid;
import pt.ist.stdf.ServerProgram.HandleClient.ClientConnection;

@Entity
@Table(name="server_epochs")
public class Epoch {
	
	@Id
	@Column(name ="epoch_id")
	public int id;
	@Transient
	public ServerGrid grid;
	
	Set<ClientEpoch> clientEpochs = new HashSet<>();
	public Epoch() {
		
	}
	public Epoch(int id) {
		grid = new ServerGrid();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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


@OneToMany(mappedBy = "primaryKey.epoch",
cascade = CascadeType.ALL)
public Set<ClientEpoch> getClientEpochs() {
	return clientEpochs;
}
public void setClientEpochs(Set<ClientEpoch> clientEpochs) {
	this.clientEpochs = clientEpochs;
}
public void addClientEpoch(ClientEpoch clientEpoch) {
    this.clientEpochs.add(clientEpoch);
}  


}
