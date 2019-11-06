package click.sonder.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyPair;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import click.sonder.crypto.AESCipherStream;
import click.sonder.crypto.RSACipher;
import click.sonder.crypto.RSAKeyPair;
import click.sonder.net.objects.*;

public class SlaveClient {
	
	/*
	 * Superclass for the whole slave client
	 */
	
	//Kryonet client object
	private Client client;
	
	//Current running directory
	private File runningDirectory;
	
	//Loaded ServerPackage
	private ServerPackage serverPackage;
	
	//Loaded local Client RSA KeyPair
	private KeyPair keyPair;
	
	//CipherStream for AES crypto
	private AESCipherStream cipherStream;
	
	//Current attached ShellExecutor
	private ShellExecutor shellExecutor;
	
	//Is there a ShellExecutor currently running
	private boolean isBusy;
	
	//Is the current connection secure and verified
	private boolean isTransportReady, isEndpointVerified;
	
	//JSON encoder/decoder
	private Gson gson;
	
	public SlaveClient() {
		//Initialize base objects
		this.client = new Client();
		this.isTransportReady = false;
		this.isBusy = true;
		this.isEndpointVerified = false;
		this.gson = new Gson();
		
		//Load resources from local files
		boolean rscLoad = loadResources();
		if(!rscLoad) {
			System.exit(-1);
		}
		
		
	}
	
	/*
	 * Connects to the server
	 * 
	 */
	private synchronized boolean connect() {
		client.start();
		try {
			//Attempt to connect to the server
			client.connect(5000, serverPackage.getRemoteAddress(), serverPackage.getRemotePort());
		} catch (IOException e) {
			//Failed to connect
			client.stop();
			e.printStackTrace();
			return false;
		}
		
		if(client.isConnected()) {
			//Start listening
			listen();
			return true;
		} else {
			return false;
		}
	}
	
	// Starts listening for server commands
	private void listen() {
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof AESKeyExchangeObject) {
					/*
					 * Switch to AES communication
					 */
					isTransportReady = buildAESStream((AESKeyExchangeObject) object);
					if(isTransportReady) {
						//AES stream built, send key acceptance code
						sendTCP(ControlCode.AES_KEY_ACCEPTED);
					} else {
						//AES stream failed to build, send reject code
						sendTCP(ControlCode.AES_KEY_REJECTED);
					}
				} else if(object instanceof ControlCode) {
					/*
					 * Handle control code
					 */
				}
				
				//Objects from here on need AES to be ready
				if(object instanceof AESData && isTransportReady) {
					AESData aesObject = (AESData) object;
					if(aesObject.getFlag() == (byte)0x2F) {
						/*
						 * instanceof RSASignatureObject
						 */
						RSASignatureObject signatureObj = (RSASignatureObject) decodeAESData(aesObject.getData(), RSASignatureObject.class);
						verifyServer(signatureObj);
					} else if(aesObject.getFlag() == (byte)0x5A) {
						/*
						 * instanceof ShellCommand
						 */
						ShellCommand commandObj = (ShellCommand) decodeAESData(aesObject.getData(), ShellCommand.class);
						if(isBusy) {
							//ShellExecutor busy, return busy code
							sendTCP(ControlCode.SHELL_BUSY);
						} else {
							//Execute command given
							if(executeCommand(commandObj)) {
								//ShellExecutor started successfully
								sendTCP(ControlCode.SHELL_STARTED);
							} else {
								//ShellExecutor failed to start
								sendTCP(ControlCode.SHELL_FAILED);
							}
						}
					} else {
						//Invalid AESData flag
						sendTCP(ControlCode.MALFORMED_DATA);
					}
				}
			}
		});
	}
	
	//Verifies the RSASignature of the endpoint
	private boolean verifyServer(RSASignatureObject signatureObject) {
		//Convert the AES key to string format
		String b64key = Base64.encodeBase64String(cipherStream.getKey());
		
		//Attempt to verify server signture
		RSACipher rsaCipher = new RSACipher();
		if(rsaCipher.verify(b64key, signatureObject.getSignature(), rsaCipher.getPublicKey(serverPackage.getRemotePublicKey()))) {
			//Signature matches, stream ready
			isEndpointVerified = true;
			isBusy = false; //Unlock ShellExecutor
			sendTCP(ControlCode.CONN_READY); //Notify server of stream ready
			return true;
		} else {
			//Failed to match signature
			isEndpointVerified = false;
			sendTCP(ControlCode.SIG_REJECTED); //Notify server of rejection
			return false;
		}
	}
	
	//Executes a ShellCommand
	private boolean executeCommand(ShellCommand commandObj) {
		//Build a new ShellExecutor and start
		this.shellExecutor = new ShellExecutor(this, commandObj.getCommand());
		this.shellExecutor.start();
		
		this.isBusy = this.shellExecutor.isAlive();
		return true; //TODO return condition?
	}
	
	//Builds the AES cipher stream once the server has passed a key
	private boolean buildAESStream(AESKeyExchangeObject keyObj) {
		//Build an RSA Cipher to decrypt the AES key
		RSACipher rsaCipher = new RSACipher();
		byte[] keyData = rsaCipher.decrypt(keyObj.getAESKey(), keyPair.getPrivate());
		byte[] iv = keyObj.getIV();
		
		//Verify contents of key object
		if(keyData.length != 32 || iv.length != 16) {
			return false;
		}
		
		//Build the AES stream
		this.cipherStream = new AESCipherStream(keyData, iv);
		this.cipherStream.start();
		
		return true;
	}

	//Loads the local resources
	private boolean loadResources() {
		//Find the running directory
		try {
			this.runningDirectory = new File(SlaveClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			//Locate local resources
			File serverPackageFile = new File(this.runningDirectory + "/server.conf");
			File keyPairFile = new File(this.runningDirectory + "/rsa.id");
			
			if(!serverPackageFile.exists() || !keyPairFile.exists()) {
				//Failed to locate resources
				return false;
			}
			
			//Load JSON files to memory objects
			this.serverPackage = gson.fromJson(FileUtils.readFileToString(serverPackageFile), ServerPackage.class);
			RSAKeyPair keyPairObj = gson.fromJson(FileUtils.readFileToString(keyPairFile), RSAKeyPair.class);
			
			//Convert JSON key pair to actual key pair
			RSACipher rsaCipher = new RSACipher();
			this.keyPair = new KeyPair(rsaCipher.getPublicKey(
					keyPairObj.getPublicKey()), rsaCipher.getPrivateKey(keyPairObj.getPrivateKey()));
			
			//Return true if object fields are filled
			return (serverPackage.getRemoteAddress() != null && serverPackage.getRemotePublicKey() != null 
					&& serverPackage.getRemotePort() != 0 && keyPair != null);
		} catch (URISyntaxException | JsonSyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Called when the ShellExecutor receives new data
	public void onShellExecutorUpdate(String shellOutput) {
		//Build a ShellOutputData object
		ShellOutputData shellOutputObject = new ShellOutputData();
		shellOutputObject.setShellOutput(shellOutput);
		
		//Send over the AES stream
		transportAES(shellOutputObject);
	}
	
	//Called when the ShellExecutor has completed
	public void onShellExecutorClose(int exitValue) {
		this.isBusy = false;
	}
	
	//Sends any object to the server
	public void sendTCP(Object obj) {
		client.sendTCP(obj);
	}
	
	//Decrypts the JSON data from an AESData object and converts it to the proper object
	private Object decodeAESData(byte[] data, Class objectType) {
		try {
			String json = new String(this.cipherStream.decrypt(data), "UTF-8");
			Object obj = gson.fromJson(json, objectType);
			return obj;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Prepares an EncodeableObject and sends it to the server
	public synchronized boolean transportAES(EncodableObject obj) {
		try {
			//Encode all object data to JSON
			byte[] jsonData = gson.toJson(obj, obj.getClass()).getBytes("UTF-8");
			
			//Encrypt JSON data
			byte[] cipherData = cipherStream.encrypt(jsonData);
			
			//Create AESData object and sends it to the server
			AESData aesObj = new AESData(obj.getTypeFlag(), cipherData);
			client.sendTCP(aesObj);
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

}
