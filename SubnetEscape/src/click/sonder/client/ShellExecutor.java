package click.sonder.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellExecutor extends Thread {
	
	/*
	 * Executes shell command and pipes out the output of
	 * the shell
	 */
	
	//Command being processed
	private String command;
	
	//SlaveClient parent to call on an shell update
	private SlaveClient parent;
	
	//Process object
	private Process process;
	
	//Construct ShellExecutor using the parent listener and a command to run
	public ShellExecutor(SlaveClient parent, String command) {
		this.parent = parent;
		this.command = command;
	}
	
	@Override
	public void start() {
		//Run the command, thread will stall out until completion
		int exitVal = execCommand();
		
		//Call the parent client with the exit value
		parent.onShellExecutorClose(exitVal);
	}
	
	//Executes the command and waits for output, returns the exit code
	private synchronized int execCommand() {
		//Build the Process
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", this.command);
		
		try {
			//Start the process
			process = processBuilder.start();
		} catch (IOException e) {
			//Failed to start process
			e.printStackTrace();
			return -999;
		}
		
		//Get a reader stream for this process hook
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		//Establish reader listening
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				//Received new shell output, call the parent client
				parent.onShellExecutorUpdate(line);
			}
		} catch (IOException e) {
			//Stream failure
			e.printStackTrace();
			return -998;
		}
		
		//Wait for process to complete
		int exitValue = -997;
		try {
			exitValue = process.waitFor();
		} catch (InterruptedException e) {
			//Process failure
			e.printStackTrace();
			return -997;
		}
		
		//Return the exit value
		return exitValue;
	}
	
	//Returns the current command
	private String getCommand() {
		return this.command;
	}

}
