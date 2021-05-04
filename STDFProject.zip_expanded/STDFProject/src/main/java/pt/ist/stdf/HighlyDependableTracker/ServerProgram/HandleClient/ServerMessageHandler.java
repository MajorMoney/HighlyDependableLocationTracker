package pt.ist.stdf.HighlyDependableTracker.ServerProgram.HandleClient;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.JsonObject;

import pt.ist.stdf.HighlyDependableTracker.ServerProgram.Server;

public class ServerMessageHandler extends Thread {

	// Mudar os códigos das msgs
//	private final int REQUEST_VALDATION = 0;
//	private final int RESPONSE_TO_VALIDATION = 1;

	private Server server;
	private ThreadPoolExecutor workers;
	private LinkedBlockingQueue<JsonObject> messages;

	public ServerMessageHandler(Server server, LinkedBlockingQueue<JsonObject> messages) {
		this.server = server;
		this.messages = messages;
		setUpWorkers();
	}

	private void setUpWorkers() {
		workers = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		workers.setCorePoolSize(1);
		workers.setMaximumPoolSize(5);
	}

	private synchronized void handleMessage(JsonObject msg) {
		int msgType;
		msgType = msg.get("msgType").getAsInt();
		if (msgType == 1) {
			Runnable task = () -> {
				// CHAMAR HANDLER
				// server.HandleX(msg)
			};
			workers.execute(task);
		} else if (msgType == 2) {
			Runnable task = () -> {
				// CHAMAR HANDLER
			};
			workers.execute(task);
		}
	}

	public void run() {

		while (true) {
			try {
				JsonObject msg = messages.take();
				handleMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
