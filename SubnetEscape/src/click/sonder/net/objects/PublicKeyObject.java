package click.sonder.net.objects;

public class PublicKeyObject implements EncodableObject {
	
	//Public key, encoded Base64
	private String publicKey;
	
	public PublicKeyObject() {}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public byte getTypeFlag() {
		return 0x2A;
	}
}
