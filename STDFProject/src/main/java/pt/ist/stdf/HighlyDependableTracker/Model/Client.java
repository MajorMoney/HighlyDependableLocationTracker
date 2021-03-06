package pt.ist.stdf.HighlyDependableTracker.Model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name="server_clients")
public class Client {
@Id
@Column(name ="client_id")
//@GeneratedValue(strategy = GenerationType.AUTO)
private Integer client_id;

@Size(min=3,max=8000)
private String publicKey;
@Size(min=3,max=8000)
private String sharedKey;
/**
@ManyToMany(cascade = CascadeType.ALL)
@JoinTable(
		name="client_epoch",
		joinColumns = {@JoinColumn(name = "client_id")},
		inverseJoinColumns = {@JoinColumn(name ="epoch_id")}
		)
Set<Epoch> epochs= new HashSet<>();**/


@OneToMany(mappedBy = "primaryKey.client",
cascade = CascadeType.ALL)
private Set<ClientEpoch> clientEpochs = new HashSet<ClientEpoch>();


public Client() {
	
}
public Client(String sharedKey, String pub) {
	this.publicKey=pub;
	this.sharedKey=sharedKey;
}

public String getSharedKey() {
	return sharedKey;
}
public void setSharedKey(String sharedKey) {
	this.sharedKey = sharedKey;
}
public String getPublicKey() {
	return publicKey;
}
public void setPublicKey(String publicKey) {
	this.publicKey = publicKey;
}
public Integer getId() {
	return client_id;
}

public void setId(Integer id) {
	this.client_id= id;
}

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
