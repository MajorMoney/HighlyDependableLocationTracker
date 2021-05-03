package pt.ist.stdf.HighlyDependableTracker.Model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="server_epochs")
public class Epoch {
	
	@Id
	@Column(name ="epoch_id")
	public int epoch_id;
	@Transient
	//public ServerGrid grid;
	
	@OneToMany(mappedBy = "primaryKey.epoch",
			cascade = CascadeType.ALL)	
	private Set<ClientEpoch> clientEpochs = new HashSet<ClientEpoch>();
	public Epoch() {
		
	}
	public Epoch(int id) {
		epoch_id=id;
		//grid = new ServerGrid();
	}
	
	public int getId() {
		return epoch_id;
	}
	public void setId(int id) {
		this.epoch_id = id;
	}
//	public Position getClientPosition(ClientConnection clientConnection)
//	{
//		Position pos = grid.getClientPosition(clientConnection);
//		return pos;
//		
//	}
//	public void AddClient(Position position, ClientConnection clientConnection)
//	{
//		grid.AddClientToGrid(position, clientConnection);
//	}



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
