package pt.ist.stdf.UserProgram.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ReportList {

	private List<JsonObject> list;

	public ReportList(int msgId) {
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
