package pt.ist.stdf.Simulation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.* ;
@Entity
@Table(name="simulated_users")
public class SimulatedUser {
@Id
private int id;
@Size(min=3,max=8000)
private String privateKey;
@Size(min=3,max=8000)
private String publicKey;

public SimulatedUser() {
	
}
public SimulatedUser(int id,String priv, String pub) {
	this.privateKey=priv;
	this.publicKey=pub; 
	this.id=id;
}
public SimulatedUser(String priv, String pub) {
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
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}



}
