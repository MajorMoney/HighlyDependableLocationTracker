package pt.ist.stdf.HighlyDependableTracker.Model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
 
@Embeddable
public class ClientEpochId implements Serializable { 
	
    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;
    @ManyToOne(cascade = CascadeType.ALL)
    private Epoch epoch;
 
    public Client getClient() {
        return client;
    }
 
    public void setClient(Client client) {
        this.client = client;
    }
 
    
    public Epoch getEpoch() {
        return epoch;
    }
 
    public void setEpoch(Epoch epoch) {
        this.epoch = epoch;
    }
}