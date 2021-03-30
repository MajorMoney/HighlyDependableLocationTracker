package pt.ist.stdf.UserProgram.Bluetooth;

import java.io.IOException;

public interface Bluetooth {
	
	public void sendBroadcastToNearby() throws IOException;
	public void testSomething();

}
