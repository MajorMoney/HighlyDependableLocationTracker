package pt.ist.stdf.UserProgram.User;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import pt.ist.stdf.CryptoUtils.CryptoUtils;

public abstract class User {
	
	private int id;
	final String serverHost;
	final int serverPort;
	private KeyPair kp;
	private SecretKey aes;
	private PublicKey serverPK;
	
	
	public User(String serverHost, int serverPort,KeyPair kp,PublicKey serverPK) {
		this.serverHost=serverHost;
		this.serverPort = serverPort;
		this.kp = kp;
		this.serverPK=serverPK;
		createAes();
		connectServer();
		getIdFromServer();
	}
	
	public KeyPair getKp() {
		return kp;
	}

	public SecretKey getAes() {
		return aes;
	}

	public PublicKey getServerPK() {
		return serverPK;
	}

	protected int getId() {
		return id;
	}
	
	private void connectServer() {
		
	}
	
	private void getIdFromServer() {
		this.id=(int)(Math.random()*100);
	}
	
	private void createAes() {
		try {
			aes = CryptoUtils.generateKeyAES();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	
	public String getServerIp() {
		return serverHost;
	}
	
	public int getServerPort() {
		return serverPort;
	}

}
