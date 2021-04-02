package pt.ist.stdf.UserProgram.User;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.JsonObject;

import pt.ist.stdf.UserProgram.Bluetooth.Bluetooth;

public class SimpleUserMessageHandler extends Thread{
	
	private final int REQUEST_VALDATION = 0;
	private final int RESPONSE_TO_VALIDATION = 1;
	
	private SimpleUser user;
	private Bluetooth bltth;
	private ThreadPoolExecutor workers;
	private LinkedBlockingQueue<JsonObject> messages;
	
	public SimpleUserMessageHandler(SimpleUser user, Bluetooth bltth) {
		this.user=user;
		this.bltth=bltth;
		
		messages= new LinkedBlockingQueue<JsonObject>() ;
		this.bltth.configureListner(messages);
		setUpWorkers();	
	}
	
	private void setUpWorkers() {
		workers = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		workers.setCorePoolSize(1);
		workers.setMaximumPoolSize(5);
	}	
	
	private void handleMessage(JsonObject msg) {
		int msgType;
		msgType=msg.get("msgType").getAsInt();
		if(msgType==REQUEST_VALDATION) {
			user.respondLocationProof(msg);
		}else if(msgType==RESPONSE_TO_VALIDATION) {
			user.hadleResponseMessage(msg);
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
