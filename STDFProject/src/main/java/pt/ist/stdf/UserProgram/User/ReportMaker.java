package pt.ist.stdf.UserProgram.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ReportMaker {

	private int msgID;
	private List<JsonObject> list;

	public ReportMaker(int msgId) {
		this.msgID = msgId;
		list = new ArrayList<JsonObject>();
	}

	public synchronized void add(JsonObject msg) {
		synchronized (list) {
			list.add(msg);
		}
	}

	public synchronized JsonArray getAllReports() {
		JsonArray reports = new JsonArray();
		synchronized (list) {
			for (JsonObject report : list) {
				reports.add(report);
			}
		}
		return reports;
	}

	public synchronized void remove() {

	}

}
