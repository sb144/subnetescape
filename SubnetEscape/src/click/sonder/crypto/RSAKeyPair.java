package click.sonder.crypto;

public class RSAKeyPair {
	
	/*
	 * JSON object to read/write RSA key pairs from the disk
	 * 
	 * Keys should be encoded Base64
	 */
	
	//Base64 encoded RSA keys
	private String publicKey, privateKey;
	
	public RSAKeyPair() {}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}
