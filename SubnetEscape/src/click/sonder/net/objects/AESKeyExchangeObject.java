package click.sonder.net.objects;

public class AESKeyExchangeObject implements EncodableObject {
	
	/*
	 * Sent from the server to the slave client to switch
	 * to using an AES channel and end RSA communication
	 */
	
	//AES key to be used, encrypted with the slave's public key, encoded Base64
	private byte[] aesKey;
	
	//Initialization vector to be used, sent without encryption (CTR mode ftw)
	private byte[] iv;
	
	public AESKeyExchangeObject() {}

	public byte[] getAESKey() {
		return aesKey;
	}

	public void setAESKey(byte[] aesKey) {
		this.aesKey = aesKey;
	}

	public byte[] getIV() {
		return iv;
	}

	public void setIV(byte[] iv) {
		this.iv = iv;
	}

	@Override
	public byte getTypeFlag() {
		return 0x4D;
	}
	
}
