package pt.ist.stdf.ServerProgram.HandleClient;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import pt.ist.stdf.CryptoUtils.CryptoUtils;
import pt.ist.stdf.ServerProgram.Position;
import pt.ist.stdf.ServerProgram.Server;
import pt.ist.stdf.Simulation.Client;
import pt.ist.stdf.Simulation.ClientEpoch;
import pt.ist.stdf.Simulation.Epoch;
import pt.ist.stdf.Simulation.SimulatedUserRepository;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.Location.Location;
import pt.ist.stdf.constants.ClientMessageTypes;

public class ClientMessage {

	private static final long serialVersionUID = 7003884428133844562L;

	/**
	 * Um clientMessageType por tipo de mensagem
	 */
	
	private byte[] buffer;
	private int userId;
	private int epoch;
	private int num_reports;
	private ClientMessageTypes msgType;
	ArrayList<ClientReport> reports;
	public Server server;
	public ClientConnection clientConnection;

	public ClientMessage(byte[] buffer, Server server, ClientConnection cc) throws Exception {
		this.buffer = buffer;
		this.server = server;
		this.clientConnection = cc;
		readMessage();
	}

	/**
	 * reads Message reader.setLenient(true) é necessario porque senao ele tenta ler
	 * espaços vazios
	 * @throws Exception 
	 */
	private void readMessage() throws Exception {
		String s = new String(buffer, StandardCharsets.UTF_8);
		// System.out.println("Received<<<<<<: "+s);
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(s));
		reader.setLenient(true);
		JsonElement tree = gson.fromJson(reader, JsonElement.class);
		// JsonElement tree = JsonParser.parseString(s).getAsJsonObject();
		if (tree.isJsonObject()) {
			JsonObject msg = tree.getAsJsonObject();
			userId = msg.get("userId").getAsInt();
			msgType = ClientMessageTypes.getMessageTypeByInt(msg.get("msgType").getAsInt());
			if (msgType != null) {

				JsonObject msgData = msg.get("msgData").getAsJsonObject();
				handleMessageData(msgType, msgData);

			}

		}
	}

	Position position;

	private void handleMessageData(ClientMessageTypes type, JsonObject msgData) throws Exception {
		switch (type) {
		case REPORT_SUBMISSION: {
			submitLocationReport(msgData);
			System.out.println("[MSG: SUBMIT LOCATION REPORT " + userId);

			break;
		}
		case obtainLocationReport:
			JsonObject response = obtainLocationReport(msgData);
			sendResponseToClient(response);
			break;
		case obtainLocationReportHA:
			JsonObject jj = obtainLocationReportHA(msgData);
			sendResponseToClient(jj);
			System.out.println("[MSG: OBTAIN USER LOCATION REPORT HA CLIENT");

			break;
		case obtainUserAtLocation:
			JsonObject resp = obtainUsersAtLocationResponse(msgData);
			sendResponseToClient(resp);
			System.out.println("[MSG: OBTAINS USER AT LOCATION " + userId + " epoch asked: " + epoch
					+ " position asked: " + position.x + " ; " + position.y);
			break;
		case submitSharedKey:
			submitSharedKey(msgData);
			break;
		}
	}

	private JsonObject obtainLocationReport(JsonObject msgData) {

		epoch = msgData.get("epoch").getAsInt();
		System.out.println("[MSG: OBTAIN LOCATION REPORT " + userId + " epoch asked: " + epoch);
		Optional<ClientEpoch> ce = server.findClientEpochByInt(server.findClientById(userId).get(),
				server.findEpochById(epoch));
		System.out.println("Obtained");

		JsonObject message = new JsonObject();
		message.addProperty("userId", userId);
		message.addProperty("msgType", ClientMessageTypes.serverResponseObtainLocationReport.getValue());
		JsonObject msgDataWithLocation = new JsonObject();
		msgDataWithLocation.addProperty("epoch", epoch);
		ClientEpoch result = ce.get();
		if (result != null)
			System.out.println("Found a non null result");
		else
			System.out.println("Found null result");
		GridLocation l = new GridLocation(result.getX_position(), result.getY_position());
		System.out.println("Possition returned : " + l.getCurrentLocation());
		msgDataWithLocation.addProperty("position", l.getCurrentLocation());
		message.add("msgData", msgDataWithLocation);
		return message;
	}

	private JsonObject obtainLocationReportHA(JsonObject msgData) {

		epoch = msgData.get("epoch").getAsInt();
		System.out.println("[MSG: OBTAIN LOCATION REPORT HA " + userId + " epoch asked: " + epoch);
		int askedUserId = msgData.get("userId").getAsInt();

		Optional<ClientEpoch> ce = server.findClientEpochByInt(server.findClientById(askedUserId).get(),
				server.findEpochById(epoch));
		System.out.println("Obtained");
		JsonObject message = new JsonObject();
		message.addProperty("userId", userId);
		message.addProperty("msgType", ClientMessageTypes.serverResponseObtainLocationReportHA.getValue());
		JsonObject msgDataWithLocation = new JsonObject();
		msgDataWithLocation.addProperty("epoch", epoch);
		msgDataWithLocation.addProperty("userId", askedUserId);
		ClientEpoch result = ce.get();
		if (result != null)
			System.out.println("Found a non null result");
		else
			System.out.println("Found null result");
		GridLocation l = new GridLocation(result.getX_position(), result.getY_position());
		System.out.println("Possition returned : " + l.getCurrentLocation());
		msgDataWithLocation.addProperty("position", l.getCurrentLocation());
		message.add("msgData", msgDataWithLocation);
		return message;
	}

	private JsonObject obtainUsersAtLocationResponse(JsonObject msgData) {
		epoch = msgData.get("epoch").getAsInt();
		String[] positionString1 = msgData.get("position").getAsString().split(" ");
		System.out.println(positionString1[0] + " " + positionString1[1]);
		position = new Position(
				Integer.parseInt(String.valueOf(positionString1[0].charAt(positionString1[0].length() - 1))),
				Integer.parseInt(String.valueOf(positionString1[1].charAt(positionString1[1].length() - 1))));
		System.out.println("Location " + position.x + " " + position.y);
		List<ClientEpoch> ce = server.getAllClientEpochs();
		System.out.println("Obtained");
		List<Client> interested = new ArrayList<Client>();
		for (ClientEpoch c : ce) {
			if (c.getX_position() == position.x)
				if (c.getY_position() == position.y)
					interested.add(server.findClientById(c.getClient().getId()).get());
		}

		JsonObject message = new JsonObject();
		message.addProperty("userId", userId);
		message.addProperty("msgType", ClientMessageTypes.serverResponseObtainUsersAtLocation.getValue());
		JsonObject msgDataWithUsers = new JsonObject();
		GridLocation l = new GridLocation(position.x, position.y);
		msgDataWithUsers.addProperty("position", l.getCurrentLocation());
		msgDataWithUsers.addProperty("epoch", epoch);
		JsonArray arr = new JsonArray();
		for (Client c : interested) {

			arr.add(c.getId());
		}
		msgDataWithUsers.add("users", arr);
		message.add("msgData", msgDataWithUsers);
		return message;
	}

	private void sendResponseToClient(JsonObject jsonObjToSend) {
		clientConnection.send(jsonObjToSend);
	}

	private void submitLocationReport(JsonObject msgData) {
		epoch = msgData.get("epoch").getAsInt();
		String[] positionString = msgData.get("position").getAsString().split(" ");
		position = new Position((int) Integer.parseInt(positionString[0].substring(positionString[0].length()-1,positionString[0].length())),
				(int) Integer.parseInt(positionString[1].substring(positionString[1].length()-1,positionString[1].length())));
		System.out.println("\n"+msgData.get("position")+"\n");
		num_reports = msgData.get("num_reports").getAsInt();
		reports = jsonArrayToList(num_reports, msgData.get("reports").getAsJsonArray());
		checkIfValidSubmitLocationReport();
	}
	
	private void submitSharedKey(JsonObject msgData) throws Exception
	{
String pubKey = server.findClientById(userId).get().getPublicKey();
		
		
		PublicKey pub = CryptoUtils.getPublicKeyFromString(pubKey);
		
		String signed = msgData.get("signedData").getAsString();
		JsonObject tobeSigned =msgData.get("toBeSigned").getAsJsonObject();
		String jsonObjStr = tobeSigned.toString();
		
		boolean b = CryptoUtils.verify(jsonObjStr, signed, pub);
		
		if(b)
			System.out.println("Received GOOOD Shared key from client "+userId+ " : "+b+ "");
		else {
			System.out.println("Naoc orreu bem a translation");
		}
		Client c = server.findClientById(userId).get();
		if (c != null)
			System.out.println("Updated/added client");
		else {
			System.out.println("DId not work");

		}
		System.out.println("Comparing to key: " + c.getSharedKey());
	//c.setSharedKey(sharedKey);
	//	server.updateClientSharedKey(c);
		System.out.println("DONE /added client");

	}

	/**
	 * Turns the jsonArrayList of reports on the submitLocationRequest into a list
	 * of ClientReports
	 * 
	 * @param jsonArray is the array of reports in the message JsonObject this will
	 *                  problably come encoded. Right now it comes in base64
	 */
	private ArrayList<ClientReport> jsonArrayToList(int num_reports, JsonArray jsonArray) {
		ArrayList<ClientReport> listz = new ArrayList<ClientReport>();
		for (int i = 0; i < num_reports; i++) {
			JsonObject rep = (JsonObject) jsonArray.get(i);
			ClientReport cr = new ClientReport(rep, this);
			System.out.println(cr.toString());
			listz.add(cr);
		}
		return listz;

	}

	private void checkIfValidSubmitLocationReport() {
		int allValid = 0;
		if (reports != null)
			for (ClientReport cr : reports) {
				if (cr.getValid()) {
					allValid++;
				}

			}
		if (allValid == num_reports) {
			ClientEpoch ce = new ClientEpoch();
			ce.setClient(server.findClientById(userId).get());
			ce.setEpoch(new Epoch(epoch));
			ce.setX_position(position.x);
			ce.setY_position(position.y);
			server.addClientEpoch(ce);
			System.out.println(
					"Add client epoch:  " + userId + " epoch: " + epoch + " x:" + position.x + " y: " + position.y);
		}

	}

	private void handleReports() {

	}

	public void PrintSubmitLocationReport() {
	
		if(reports!=null)
		for (ClientReport b : reports) {
			System.out.println(b.toString());
		}
				
	}

	@Override
	public String toString() {
		String s = "[MESSAGE: ] id: " + userId + " msgType: " + msgType.toString();
		return s;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getEpoch() {
		return epoch;
	}

	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}

	public ClientMessageTypes getMsgType() {
		return msgType;
	}

	public void setMsgType(ClientMessageTypes msgType) {
		this.msgType = msgType;
	}

	public ArrayList<ClientReport> getReports() {
		return reports;
	}

	public void setReports(ArrayList<ClientReport> reports) {
		this.reports = reports;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	

}
