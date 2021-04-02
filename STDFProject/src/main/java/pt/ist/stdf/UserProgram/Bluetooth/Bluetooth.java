package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;

public interface Bluetooth {
	
	public void configureListner(LinkedBlockingQueue messages);
	public void respondRequest(JsonObject msg);
	public void sendBroadcastToNearby(String msg) throws IOException;

}
