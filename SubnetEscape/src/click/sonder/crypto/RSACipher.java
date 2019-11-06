package click.sonder.crypto;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSACipher {
	
	/*
	 * Specialized Cipher object for RSA crypto
	 */
	
	//RSA Cipher constants
	static final String ALG = "RSA/ECB/PKCS1Padding", SIGNATURE_ALG = "SHA256withRSA";
	static final int KEY_LENGTH = 2048;
	
	//Construct a new RSACipher
	public RSACipher() {
		
	}
	
	//Converts a string public key (Base64 encoded) to a public key object
	public PublicKey getPublicKey(String keyString) {
		try {
			byte[] keyBytes = Base64.decodeBase64(keyString.getBytes("UTF-8"));
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
			return kf.generatePublic(spec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Converts a string private key (Base64 encoded) to a private key object
	public PrivateKey getPrivateKey(String keyString) {
		try {
			byte[] keyBytes = Base64.decodeBase64(keyString.getBytes("UTF-8"));
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
			return kf.generatePrivate(spec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Encrypt using a public key, encodes output as Base64
	public byte[] encrypt(byte[] data, PublicKey key) {
		try {
			Cipher cipher = Cipher.getInstance(ALG, "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cipherData = cipher.doFinal(data);
			return Base64.encodeBase64(cipherData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Decrypt using a private key, assumes data is Base64 encoded
	public byte[] decrypt(byte[] data, PrivateKey key) {
		try {
			Cipher cipher = Cipher.getInstance(ALG, "BC");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(Base64.decodeBase64(data));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Sign a message with a private key, returns Base64 string
	public String sign(String message, PrivateKey key) {
		try {
			Signature privateSignature = Signature.getInstance(SIGNATURE_ALG, "BC");
		    privateSignature.initSign(key);
		    privateSignature.update(message.getBytes("UTF_8"));
		    
		    byte[] signature = privateSignature.sign();
		    return Base64.encodeBase64String(signature);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Verify a message with a public key, assumes signature is Base64 encoded
	public boolean verify(String message, String signature, PublicKey key) {
		try {
			Signature publicSignature = Signature.getInstance(SIGNATURE_ALG, "BC");
		    publicSignature.initVerify(key);
		    publicSignature.update(message.getBytes("UTF-8"));
		    
		    byte[] signatureData = Base64.decodeBase64(signature);
		    return publicSignature.verify(signatureData);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
