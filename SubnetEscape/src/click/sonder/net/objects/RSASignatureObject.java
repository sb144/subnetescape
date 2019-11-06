package click.sonder.net.objects;

public class RSASignatureObject implements EncodableObject {
	
	/*
	 * Sent from the server to the client over the newly established
	 * AES channel for the client to verify the endpoint
	 */
	
	//RSA signature string
	private String signature;
	
	public RSASignatureObject() {}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public byte getTypeFlag() {
		return 0x2F;
	}
}
