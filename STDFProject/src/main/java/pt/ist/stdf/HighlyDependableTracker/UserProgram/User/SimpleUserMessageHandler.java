package pt.ist.stdf.HighlyDependableTracker.UserProgram.User;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.JsonObject;

import pt.ist.stdf.HighlyDependableTracker.UserProgram.Bluetooth.Bluetooth;

public class SimpleUserMessageHandler extends Thread{
	
	private final int REQUEST_VALDATION = 0;
	private final int RESPONSE_TO_VALIDATION = 1;
	
	//Server msgs
	private final int SERVER_RESPONSE_OBTAIN_LOCATION_REPORT=6;
	private final int SERVER_RESPONSE_OBTAIN_USERS_AT_LOCATION=7;
	private final int SERVER_RESPONSE_OBTAIN_LOCATION_REPORT_HA=8;
	
	private SimpleUser user;
	private Bluetooth bltth;
	private ThreadPoolExecutor workers;
	private LinkedBlockingQueue<JsonObject> messages;
	
	public SimpleUserMessageHandler(SimpleUser user, Bluetooth bltth) {
		this.user=user;
		this.bltth=bltth;
		
		messages= new LinkedBlockingQueue<JsonObject>() ;
		this.bltth.configureListner(messages);
		
		try {
			user.starServerListener(messages);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setUpWorkers();	
	}
	
	private void setUpWorkers() {
		workers = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		workers.setCorePoolSize(1);
		workers.setMaximumPoolSize(5);
	}	
	
	private synchronized void handleMessage(JsonObject msg) {
		int msgType;
		msgType=msg.get("msgType").getAsInt();
		if(msgType==REQUEST_VALDATION) {
//			System.out.println("RECEIVED REQUEST_VALDATION");
			Runnable task = () -> {
				user.respondLocationProof(msg);
			};
			workers.execute(task);
			
		}else if(msgType==RESPONSE_TO_VALIDATION) {
			Runnable task = () -> {
				user.hadleResponseMessage(msg);
			};
			workers.execute(task);
		}
		else if(msgType==SERVER_RESPONSE_OBTAIN_LOCATION_REPORT) {
			System.out.println("RECEIVED ON HANDLE MESSAGE");
			Runnable task = () -> {
				user.handleServerResponseObtainLocRepMessage(msg);
			};
			workers.execute(task);
		}
		else if(msgType==SERVER_RESPONSE_OBTAIN_USERS_AT_LOCATION) {
			System.out.println("RECEIVED ON HANDLE MESSAGE");
			Runnable task = () -> {
				user.handleServerResponseObtainUsersAtLocationMessage(msg);
			};
			workers.execute(task);
		}
		else if(msgType==SERVER_RESPONSE_OBTAIN_LOCATION_REPORT_HA) {
			System.out.println("RECEIVED ON SERVER_RESPONSE_OBTAIN_LOCATION_REPORT_HA");
			Runnable task = () -> {
				user.handleServerResponseObtainLocationMessageHA(msg);
			};
			workers.execute(task);
		}
	}
	
	public void run(){
		
		while(true) {
			try {
				JsonObject msg = messages.take();
				handleMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}

}
