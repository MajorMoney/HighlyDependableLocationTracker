package pt.ist.stdf.HighlyDependableTracker.Model;


import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import pt.ist.stdf.HighlyDependableTracker.ServerProgram.Position;
import pt.ist.stdf.HighlyDependableTracker.ServerProgram.HandleClient.ClientConnection;

@AssociationOverrides({
    @AssociationOverride(name = "primaryKey.client",
        joinColumns = @JoinColumn(name = "client_id")),
    @AssociationOverride(name = "primaryKey.epoch",
        joinColumns = @JoinColumn(name = "epoch_id")) })
@Entity
@Table(name="server_client_epochs")
public class ClientEpoch {
	
	//Composite id key

	@EmbeddedId
	private ClientEpochId primaryKey = new ClientEpochId();
	
	//@Transient
	//public ServerGrid grid;

	@Column(name="x")
	private int x_position;

	@Column(name="y")
	private int y_position;
	
	

	public ClientEpoch() {
		
	}

	public ClientEpoch(int id) {
		//grid = new ServerGrid();
	}
	
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
	 
	    public int getX_position() {
		return x_position;
	}

	public void setX_position(int x_position) {
		this.x_position = x_position;
	}

	public int getY_position() {
		return y_position;
	}

	public void setY_position(int y_position) {
		this.y_position = y_position;
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
	    
	@Override
		public String toString() {
			return "ClientEpoch [primaryKey=" + primaryKey + ", x_position=" + x_position + ", y_position=" + y_position
					+ "]";
		}

	public Position getClientPosition(ClientConnection clientConnection)
	{
		//Position pos = grid.getClientPosition(clientConnection);
		//return pos;
		return null;
	}
	public void AddClient(Position position, ClientConnection clientConnection)
	{
	//	grid.AddClientToGrid(position, clientConnection);
	}

}
