package pt.ist.stdf.Simulation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="simulated_servers")
public class SimulatedServer {
@Id
private Integer id;
private String privateKey;
private String publicKey;

public SimulatedServer() {
	
}
public SimulatedServer(String priv, String pub) {
	this.privateKey=priv;
	this.publicKey=pub; 
}

public String getPrivateKey() {
	return privateKey;
}
public void setPrivateKey(String privateKey) {
	this.privateKey = privateKey;
}
public String getPublicKey() {
	return publicKey;
}
public void setPublicKey(String publicKey) {
	this.publicKey = publicKey;
}
public Integer getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}


}
