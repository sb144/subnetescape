package click.sonder.client;

public class ServerPackage {
	
	/*
	 * JSON object loaded by the client that contains the server
	 * to connect to along with the server's public key
	 */
	
	//DNS resolved name of the server
	private String domainName;
	
	//IP address of the server
	private String remoteAddress;
	
	//TCP port used
	private int remotePort;
	
	//Base64 encoded public key
	private String remotePublicKey;
	
	public ServerPackage() {}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public String getRemotePublicKey() {
		return remotePublicKey;
	}

	public void setRemotePublicKey(String remotePublicKey) {
		this.remotePublicKey = remotePublicKey;
	}
	
	

}
