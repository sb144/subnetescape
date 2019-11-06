package click.sonder.net.objects;

public class ShellCommand implements EncodableObject {
	
	/*
	 * Command to be executed by a slave client
	 */
	
	//Command to be executed
	private String command;
	
	public ShellCommand() {}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public byte getTypeFlag() {
		return 0x5A;
	}
	
	

}
