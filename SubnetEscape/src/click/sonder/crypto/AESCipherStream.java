package click.sonder.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESCipherStream extends Thread {
	
	/*
	 * Threaded AES cipher stream for reusing Cipher objects
	 */
	
	//AES Cipher constants
	static final String ALG = "AES/CTR/NoPadding";
	static final int KEY_LENGTH = 256;
	
	//Dual operation Ciphers
	private Cipher encryptCipher, decryptCipher;
	
	//AES key data
	private byte[] key;
	
	//Initialization Vector
	private byte[] iv;
	
	//Construct a AESCipherStream using a AES key and an 16-byte IV
	public AESCipherStream(byte[] key, byte[] iv) {
		this.key = key;
		this.iv = iv;
	}
	
	@Override
	public void start() {
		buildCiphers();
	}
	
	//Encrypts data and encodes output as Base64
	public synchronized byte[] encrypt(byte[] data) {
		try {
			return Base64.encodeBase64(encryptCipher.doFinal(data));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Decrypts data, assumes input data is encoded Base64
	public synchronized byte[] decrypt(byte[] data) {
		try {
			return decryptCipher.doFinal(Base64.decodeBase64(data));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Builds the Cipher objects
	private boolean buildCiphers() {
		try {
			//Get Key object from key bytes
			SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
			
			//Build IvParameterSpec
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			
			//Build Cipher objects
			encryptCipher = Cipher.getInstance(ALG, "BC");
			decryptCipher = Cipher.getInstance(ALG, "BC");
			encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
			decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
			
			return (encryptCipher != null && decryptCipher != null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public byte[] getKey() {
		return key;
	}

	public byte[] getIV() {
		return iv;
	}

}
