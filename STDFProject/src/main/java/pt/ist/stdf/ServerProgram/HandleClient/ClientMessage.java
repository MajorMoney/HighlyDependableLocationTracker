package pt.ist.stdf.ServerProgram.HandleClient;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import pt.ist.stdf.ServerProgram.Position;

public class ClientMessage {

	private static final long serialVersionUID = 7003884428133844562L;
	/**
	 * Um clientMessageType por tipo de mensagem 
	 * */
	public enum ClientMessageTypes {
		REPORT_SUBMISSION(3), obtainLocationReport(1), obtainUserAtLocation(2),
		userReport(0);

		private final int value;

		private ClientMessageTypes(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
		/**@param val comes in the messages
		 * @return the correct ClientMessageType*/
		public static ClientMessageTypes getMessageTypeByInt(int val) {
			switch (val) {
			case 0:
				return ClientMessageTypes.REPORT_SUBMISSION;
			case 1:
				return ClientMessageTypes.obtainLocationReport;
			case 2:
				return ClientMessageTypes.obtainUserAtLocation;
			case 3:
				return ClientMessageTypes.userReport;
			default:
				return null;
			}
		}

	}

	private byte[] buffer;
	private int userId;
	private int epoch;
	private ClientMessageTypes msgType;
	ArrayList<ClientReport> reports;
	
	public ClientMessage(byte[] buffer) {
		this.buffer = buffer;
		readMessage();
	}
	/**
	 * reads Message
	 * reader.setLenient(true) é necessario porque senao ele tenta ler espaços vazios
	 * */
	private void readMessage() {
//		String s = new String(buffer, StandardCharsets.UTF_8);
//		System.out.println("Received: "+s);
//		Gson gson = new Gson();
//		JsonReader reader = new JsonReader(new StringReader(s));
//		reader.setLenient(true);
//		JsonElement tree = gson.fromJson(reader, JsonElement.class);
//		//JsonElement tree = JsonParser.parseString(s).getAsJsonObject();
//		if (tree.isJsonObject()) {
//			JsonObject msg = tree.getAsJsonObject();
//			userId = msg.get("userId").getAsInt();
//			msgType = ClientMessageTypes.getMessageTypeByInt(msg.get("msgType").getAsInt());
//			if (msgType != null) {
//
//				JsonObject msgData = msg.get("msgData").getAsJsonObject();
//				handleMessageData(msgType, msgData);
//
//			}
//
//		}
	}
	
	Position position;
	private void handleMessageData(ClientMessageTypes type, JsonObject msgData) {
		switch(type) {
		case REPORT_SUBMISSION:
		{
			epoch = msgData.get("epoch").getAsInt();
			position = new Position(msgData.get("position").getAsJsonArray());
			int num_reports = msgData.get("num_reports").getAsInt();
			reports = jsonArrayToList(num_reports,msgData.get("reports").getAsJsonArray());
			break;
		}
		case obtainLocationReport:
			epoch = msgData.getAsInt();
			break;
		case obtainUserAtLocation:
		}
	}
	/**
	 * Turns the jsonArrayList of reports on the submitLocationRequest into a list of ClientReports
	 * @param jsonArray is the array of reports in the message JsonObject 
	 * this will problably come encoded. Right now it comes in base64*/
	private ArrayList<ClientReport> jsonArrayToList(int num_reports,JsonArray jsonArray){
		ArrayList<ClientReport> listz = new ArrayList<ClientReport>();
		for(int i=0; i <num_reports;i++) {
			byte[] a = Base64.getDecoder().decode(jsonArray.get(i).getAsString());
			listz.add(new ClientReport(a));
		}
		return listz;
		
	}

	private void handleReports() {
		
	}
	
	
	public void PrintSubmitLocationReport() {
		String s = "[MESSAGE: ] id: "+userId+" msgType: "+msgType.toString() +" epoch: "+
				epoch +" position: " + position.toString() +"/n";
		System.out.println(s);
		for (ClientReport b : reports) {
			System.out.println(b.toString());
		}
				
	}
	@Override
	public String toString() {
		String s = "[MESSAGE: ] id: "+userId+" msgType: "+msgType.toString();
		return s;
	}
	
	
	
	
	
	
	
	
	
	
	
}
