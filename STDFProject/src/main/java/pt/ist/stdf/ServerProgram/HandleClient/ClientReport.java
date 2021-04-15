package pt.ist.stdf.ServerProgram.HandleClient;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import pt.ist.stdf.CryptoUtils.CryptoUtils;
import pt.ist.stdf.ServerProgram.Position;
import pt.ist.stdf.constants.ClientMessageTypes;

public class ClientReport {

	private byte[] buffer;
	private int userId;
	private int epoch;
	private ClientMessageTypes msgType;
	private Position position;
	private ClientMessage cm;
	boolean valid = true;

	public ClientReport(JsonObject json, ClientMessage cm) {
		this.cm = cm;
		readMessageFromJson(json);
	}

	private void readMessageFromJson(JsonObject json) {
		JsonObject msg = json;
		userId = msg.get("userId").getAsInt();
		msgType = ClientMessageTypes.getMessageTypeByInt(msg.get("msgType").getAsInt());
		if (msgType.equals(ClientMessageTypes.userReport)) {
			JsonObject msgData = msg.get("msgData").getAsJsonObject();
			String signature = msg.get("signature").getAsString();
			handleMessageData(msgData, signature);

		}
	}

	private void handleMessageData(JsonObject msgData, String signature) {
		System.out.println(msgData);
		epoch = msgData.get("epoch").getAsInt();
		verifySender(msgData.get("signer"));
		verifySingner(msgData, signature);
	}

	private void verifySender(JsonElement jsonElement) {
		int sender = jsonElement.getAsInt();
		if (sender != userId)
			isNotValid();
	}

	private void verifySingner(JsonObject msgData, String signature) {
		String pks = cm.server.findClientById(userId).get().getPublicKey();
		PublicKey pk = CryptoUtils.getPublicKeyFromString(pks);
		try {
			
			String jsonObjStr = msgData.toString();
			boolean b = CryptoUtils.verify(jsonObjStr, signature, pk);

			if (!b)
				isNotValid();
			System.out.println("SERVER - Verified " + b + " signature from signer: " + userId
					+ " on Message from user ID: " + cm.getUserId());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void isNotValid() {
		valid = false;
	}

	public boolean getValid() {
		return valid;
	}

	@Override
	public String toString() {
		String s = "[CLIENT REPORT] id: " + userId + " msgType: " + msgType.toString() + " epoch: " + epoch;
		return s;
	}

}