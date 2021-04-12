package pt.ist.stdf.ServerProgram.database;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
 
@Embeddable
public class ClientEpochId implements Serializable { 
    private Client client;
    private Epoch epoch;
 
    @ManyToOne(cascade = CascadeType.ALL)
    public Client getClient() {
        return client;
    }
 
    public void setClient(Client client) {
        this.client = client;
    }
 
    @ManyToOne(cascade = CascadeType.ALL)
    public Epoch getEpoch() {
        return epoch;
    }
 
    public void setEpoch(Epoch epoch) {
        this.epoch = epoch;
    }
}