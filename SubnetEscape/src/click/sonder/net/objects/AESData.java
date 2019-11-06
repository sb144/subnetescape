package click.sonder.net.objects;

public class AESData {
	
	/*
	 * Network object containing a byte type for the type of object
	 * this data represents and the byte[] of object data
	 */
	
	//Object type flag
	private byte flag;
	
	//Encrypted object data
	private byte[] data;
	
	public AESData() {}
	
	public AESData(byte flag, byte[] data) {
		this.flag = flag;
		this.data = data;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
