package pt.ist.stdf.UserProgram.User;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Random;

import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;
import pt.ist.stdf.UserProgram.Location.GridLocation;
import pt.ist.stdf.UserProgram.Location.Location;
import pt.ist.stdf.constants.ClientMessageTypes;

public class HAUser extends SimpleUser{

	public HAUser(String serverHost, int serverPort, Location loc, Bluetooth bltth, KeyPair kp, PublicKey serverPK,
			int id) {
		super(serverHost, serverPort, loc, bltth, kp, serverPK, id);
		// TODO Auto-generated constructor stub
	}


	public JsonObject generateObtainUsersAtLocationHA() {
		JsonObject msgData = new JsonObject();
		Random rand = new Random();
		GridLocation g = new GridLocation(4, 4);
		msgData.addProperty("position", g.getCurrentLocation());
		msgData.addProperty("epoch", 1);
		JsonObject obj = new JsonObject();
		obj.addProperty("msgType", ClientMessageTypes.serverResponseObtainUsersAtLocation.getValue());
		obj.add("msgData", msgData);
		obj.addProperty("userId", getId());

		return obj;
	}

	public JsonObject generateObtainLocationReportHA() {
		JsonObject msgData = new JsonObject();
		msgData.addProperty("epoch", getEpoch());
		msgData.addProperty("userId", 4);
		JsonObject obj = new JsonObject();
		obj.addProperty("msgType", ClientMessageTypes.serverResponseObtainLocationReportHA.getValue());
		obj.addProperty("userId", getId());
		obj.add("msgData", msgData);

		return obj;
	}

	
}
