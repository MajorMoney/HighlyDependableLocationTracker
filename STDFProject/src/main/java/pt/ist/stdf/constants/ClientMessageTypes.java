package pt.ist.stdf.constants;


public enum ClientMessageTypes{ 
	userReport(1),
	obtainUserAtLocation(2),
	REPORT_SUBMISSION(3),
	obtainLocationReportHA(4),
	obtainLocationReport(5),
	serverResponseObtainLocationReport(6),
	serverResponseObtainUsersAtLocation(7),
	serverResponseObtainLocationReportHA(8),
	submitSharedKey(9);

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
		case 1:
			return ClientMessageTypes.userReport;
		case 2:
			return ClientMessageTypes.obtainUserAtLocation;
		case 3:
			return ClientMessageTypes.REPORT_SUBMISSION;
		case 4:
			return ClientMessageTypes.obtainLocationReportHA;
		case 5:
			return ClientMessageTypes.obtainLocationReport;
		case 6:
			return ClientMessageTypes.serverResponseObtainLocationReport;
		case 7:
			return ClientMessageTypes.serverResponseObtainUsersAtLocation;
		case 8:
			return ClientMessageTypes.serverResponseObtainLocationReportHA;
		case 9:
			return ClientMessageTypes.submitSharedKey;
		
		default:
			return null;
		}
	}

}
