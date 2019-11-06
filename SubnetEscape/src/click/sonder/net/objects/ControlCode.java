package click.sonder.net.objects;

public enum ControlCode {
	
	/*
	 * Object used for simple communication messages
	 */
	
	//Group 0x1A - Authentication protocol
	AES_KEY_ACCEPTED((byte)0x1A, (byte)0x01), AES_KEY_REJECTED((byte)0x1A, (byte)0x1F),
	CONN_READY((byte)0x1A, (byte)0x3C), SIG_REJECTED((byte)0x1A, (byte)0x2F),
	
	//Group 0x2B - Shell access
	SHELL_BUSY((byte)0x2B, (byte)0x3F), SHELL_DONE_SUCCESS((byte)0x2B, (byte)0x2D),
	SHELL_DONE_ERROR((byte)0x2B, (byte)0x4C), SHELL_FAILED((byte)0x2B, (byte)0x5B),
	SHELL_STARTED((byte)0x2B, (byte)0x01),
	
	//Group 0x3C - Network Status
	MALFORMED_DATA((byte)0x3C, (byte)0x6F);
	
	byte group, code;
	
	ControlCode(byte group, byte code) {
		this.group = group;
		this.code = code;
	}

	public byte getGroup() {
		return group;
	}

	public byte getCode() {
		return code;
	}

}
