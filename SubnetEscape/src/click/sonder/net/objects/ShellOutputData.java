package click.sonder.net.objects;

public class ShellOutputData implements EncodableObject {

	/*
	 * Sent from a slave to a control server, contains a string output
	 * from the shell
	 */
	
	//Shell output
	private String shellOutput;
	
	public ShellOutputData() {}
	
	public String getShellOutput() {
		return shellOutput;
	}

	public void setShellOutput(String shellOutput) {
		this.shellOutput = shellOutput;
	}

	@Override
	public byte getTypeFlag() {
		return 0x1A;
	}

}
