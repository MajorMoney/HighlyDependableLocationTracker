package pt.ist.stdf.Simulation;

import java.security.KeyPair;
import java.security.PublicKey;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.stdf.CryptoUtils.CryptoUtils;
import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Bluetooth.BluetoothSimulation;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.User.SimpleUser;

public class SimpleUserSimulation extends SimpleUser {

	public SimpleUserSimulation(String serverHost, int serverPort, GridLocation loc, Bluetooth bltth,
			int numEpochToSimulate, KeyPair kp, PublicKey serverPK, int id) {
		super(serverHost, serverPort, loc, bltth, kp, serverPK, id);
	}

	public static int convertPosToBluetoothPort(int x, int y) {
		return Simulation.BLUETOOTH_PORT + y * Simulation.GRID_X + x;
	}

	public void _move(int x, int y) {
		((GridLocation) this.loc).setPosition(x, y);
		((BluetoothSimulation) this.bltth).changePort(convertPosToBluetoothPort(x, y));
	}

	public void advanceEpoch() {
		setEpoch(getEpoch() + 1);
	}

	public void _requestProof() {
		this.requestLocationProof();// this will requestLocationProof(), wait for responses in a given time and then
		// submitLocationReport()		
	}

	private void _submitLocationToServe() {

	}

	private void _prepareEmulation() {

	}

	public void startSimulation(int type)
			throws InterruptedException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		switch (type) {
		case 0:
			_requestProof();
			break;
		case 1:
			Thread.sleep(1000);
			_requestProof();
			break;
		case 2:
			Thread.sleep(3000);
			_requestProof();
			break;
		case 3:
			// JsonObject jo = generateObtainUsersAtLocationHA();
			// submitLocationReport(jo);
			System.out.println("[HA CLIENT] Obtain users @ location");
			break;
		case 4:
			JsonObject joo;
			try {
				joo = generateSubmitSharedKeyTest();
				submitLocationReport(joo);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("[CLIENT] Submit shared key");
			break;
		case 5:
			System.out.println("[HA CLIENT] Obtain location report");
			break;

		default:
			break;
		}
	}

}
