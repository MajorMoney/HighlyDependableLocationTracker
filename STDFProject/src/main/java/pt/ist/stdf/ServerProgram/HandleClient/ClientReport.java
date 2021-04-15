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
import pt.ist.stdf.ServerProgram.HandleClient.ClientMessage.ClientMessageTypes;

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
			byte[] signature = Base64.getMimeDecoder().decode(msg.get("signature").toString().getBytes());
			String decodedMime = new String(signature);
		System.out.println(msg.get("pk").toString());
				handleMessageData(msgData, decodedMime);
		

			
			
		}
	}

	private void readMessage() {
		String s = new String(buffer, StandardCharsets.UTF_8);
		System.out.println("Report: " + s);
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(s));
		reader.setLenient(true);
		JsonElement tree = gson.fromJson(reader, JsonElement.class);
		// JsonElement tree = JsonParser.parseString(s).getAsJsonObject();
		if (tree.isJsonObject()) {
			JsonObject msg = tree.getAsJsonObject();
			userId = msg.get("userId").getAsInt();
			msgType = ClientMessageTypes.getMessageTypeByInt(msg.get("msgType").getAsInt());
			if (msgType.equals(ClientMessageTypes.userReport)) {
				JsonObject msgData = msg.get("msgData").getAsJsonObject();
				//handleMessageData(msgData);
			} else
				System.out.println("Invalid report");

		}
	}

	private void handleMessageData(JsonObject msgData,String decodedMime) {
		System.out.println(msgData);
		epoch = msgData.get("epoch").getAsInt();
		verifySender(msgData.get("signer"));
		verifySingner(msgData, decodedMime);
	}

	private void verifySender(JsonElement jsonElement) {
		int sender = jsonElement.getAsInt();
		if (sender != userId)
			isNotValid();
	}

	private void verifySingner(JsonObject msgData,String decodedMime) {
		String pks = cm.server.findClientById(userId).get().getPublicKey();
		PublicKey pk = CryptoUtils.getPublicKeyFromString(pks);
		System.out.println(pk.toString());
		try {
			byte[] data =msgData.toString().getBytes(); 
			//CryptoUtils.preHash(data);
			
			if (CryptoUtils.verifySignedMessagedRSA(data,decodedMime.getBytes(), pk))
				System.out.println("SERVER - Verified signature from signer: " + userId + " on Message from user ID: "
						+ cm.getUserId());
			else
				isNotValid();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
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