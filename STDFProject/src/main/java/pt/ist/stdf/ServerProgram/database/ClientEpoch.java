package pt.ist.stdf.ServerProgram.database;

import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import pt.ist.stdf.ServerProgram.Position;
import pt.ist.stdf.ServerProgram.ServerGrid;
import pt.ist.stdf.ServerProgram.HandleClient.ClientConnection;

@Entity
@Table(name="server_client_epochs")
@AssociationOverrides({
    @AssociationOverride(name = "primaryKey.client",
        joinColumns = @JoinColumn(name = "client_id")),
    @AssociationOverride(name = "primaryKey.epoch",
        joinColumns = @JoinColumn(name = "epoch_id")) })
public class ClientEpoch {
	
	//Composite id key
	private ClientEpochId primaryKey = new ClientEpochId();
	
	@Transient
	public ServerGrid grid;
	
	private int x_position;
	private int y_position;
	
	

	public ClientEpoch() {
		
	}

	public ClientEpoch(int id) {
		grid = new ServerGrid();
	}
	
	@EmbeddedId
	public ClientEpochId getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(ClientEpochId id) {
		this.primaryKey = id;
	}
	
	 @Transient
	    public Client getClient() {
	        return getPrimaryKey().getClient();
	    }
	 
	    public void setClient(Client client) {
	        getPrimaryKey().setClient(client);
	    }
	 
	    @Transient
	    public Epoch getEpoch() {
	        return getPrimaryKey().getEpoch();
	    }
	 
	    public void setEpoch(Epoch epoch) {
	        getPrimaryKey().setEpoch(epoch);
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
