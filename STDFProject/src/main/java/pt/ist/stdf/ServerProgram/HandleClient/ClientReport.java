package pt.ist.stdf.ServerProgram.HandleClient;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import pt.ist.stdf.ServerProgram.Position;
import pt.ist.stdf.ServerProgram.HandleClient.ClientMessage.ClientMessageTypes;

public class ClientReport {

	private byte[] buffer;
	private int userId;
	private int epoch;
	private ClientMessageTypes msgType;
	private Position position;

	public ClientReport(byte[] arr) {
		this.buffer = arr;
		readMessage();
	}

	public ClientReport(JsonObject json)
	{
		readMessageFromJson(json);
	}
	private void readMessageFromJson(JsonObject json) {
		JsonObject msg=json;
		userId = msg.get("userId").getAsInt();
		msgType = ClientMessageTypes.getMessageTypeByInt(msg.get("msgType").getAsInt());
		if (msgType.equals(ClientMessageTypes.userReport)) {
			JsonObject msgData = msg.get("msgData").getAsJsonObject();
			handleMessageData(msgData);
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
				handleMessageData(msgData);
			}
			else
				System.out.println("Invalid report");

		}
	}
	private void handleMessageData(JsonObject msgData) {
			epoch = msgData.get("epoch").getAsInt();
			verifySingner();
	}
	
	private void verifySingner() {
		
	}

	public boolean isValid() {
		return true;
	}
	
	@Override
	public String toString() {
		String s = "[CLIENT REPORT] id: "+userId+" msgType: "+msgType.toString() 
		+" epoch: "+epoch;
		return s;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}