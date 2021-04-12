package pt.ist.stdf.ServerProgram.database;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;


@Entity
@Table(name="server_clients")
public class Client {
@Id
@Column(name ="client_id")
@GeneratedValue(strategy = GenerationType.AUTO)
private Integer id;

private String publicKey;
/**
@ManyToMany(cascade = CascadeType.ALL)
@JoinTable(
		name="client_epoch",
		joinColumns = {@JoinColumn(name = "client_id")},
		inverseJoinColumns = {@JoinColumn(name ="epoch_id")}
		)
Set<Epoch> epochs= new HashSet<>();**/

private Set<ClientEpoch> clientEpochs = new HashSet<>();


public Client() {
	
}
public Client(String priv, String pub) {
	this.publicKey=pub;
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


@OneToMany(mappedBy = "primaryKey.client",
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
