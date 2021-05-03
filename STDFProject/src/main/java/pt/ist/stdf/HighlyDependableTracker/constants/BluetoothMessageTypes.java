package pt.ist.stdf.HighlyDependableTracker.constants;


public enum BluetoothMessageTypes{ 
	 REQUEST_VALIDATION(0),
	 RESPONSE_TO_VALIDATION(1);

	private final int value;

	private BluetoothMessageTypes(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}public static BluetoothMessageTypes getMessageTypeByInt(int val) {
		switch (val) {
		case 0:
			return BluetoothMessageTypes.REQUEST_VALIDATION;
		case 1:
			return BluetoothMessageTypes.RESPONSE_TO_VALIDATION;
		default:
			return null;
		}		}
	

}
