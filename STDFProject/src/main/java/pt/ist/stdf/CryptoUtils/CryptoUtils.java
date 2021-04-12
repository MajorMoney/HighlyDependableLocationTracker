package pt.ist.stdf.CryptoUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		return kp;
	}

	public static SecretKey generateKeyAES() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		SecretKey key = keyGenerator.generateKey();
		System.out.println("AES KEY: " + new String(key.getEncoded(), 0, key.getEncoded().length));
		return key;
	}

	public static void generateStrongSecureRandom(byte[] nextBytes) throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstanceStrong();
		sr.nextBytes(nextBytes);
	}

	public static IvParameterSpec generateIv() throws NoSuchAlgorithmException {
		byte[] iv = new byte[16];
		generateStrongSecureRandom(iv);
		System.out.println("IV: " + new String(iv, 0, iv.length));
		return new IvParameterSpec(iv);
	}

	public static void preHash(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md=MessageDigest.getInstance("MD5");
		md.update(data);
		md.digest();
	}
	
	public static byte[] signMessageRSA(byte[] data, PrivateKey pk)
			throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		// generating a signature
		Signature dsaForSign = Signature.getInstance("SHA512withRSA");
		dsaForSign.initSign(pk);
		dsaForSign.update(data);
		byte[] signature = dsaForSign.sign();
		return signature;
	}

	public static boolean verifySignedMessagedRSA(byte[] data, byte[] signature, PublicKey pk)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		// verifying a signature
		Signature dsaForVerify = Signature.getInstance("SHA512withRSA");
		dsaForVerify.initVerify(pk);
		dsaForVerify.update(data);
		return dsaForVerify.verify(signature);
	}

	public static byte[] cipherKey(byte[] key,PrivateKey pk) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		c.init(Cipher.ENCRYPT_MODE, pk);
		return c.doFinal(key);
	}

	public static byte[] decipherKey(byte[] key, PublicKey pk) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, pk);
		return cipher.doFinal(key);
	}

	public static String cipherMsg(String msg, SecretKey key,IvParameterSpec iv) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(msg.getBytes());
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decipherMsg(String cipher, SecretKey key,IvParameterSpec iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, key,iv);
		byte[] plainText = c.doFinal(Base64.getDecoder().decode(cipher));
		return new String(plainText);
	}

	public static void main(String args[]) {
		try {
			byte[] data = "Data to be signed".getBytes();
			KeyPair kp = generateKeyPair();
			SecretKey aes=generateKeyAES();
			PublicKey publicKey = kp.getPublic();
			PrivateKey privateKey = kp.getPrivate();
			IvParameterSpec iv=generateIv();
			
			String text = new String(data, 0, data.length);
			byte[] encAes = cipherKey(aes.getEncoded(), privateKey );
			System.out.println("CIPHER AES : " + new String(encAes, 0, encAes.length) + "\n");
			byte[] decAes =decipherKey(encAes,publicKey );
			SecretKey originalKey = new SecretKeySpec(decAes, 0, decAes.length, "AES");
			System.out.println("PLAITEXT AES : " + new String(decAes, 0, decAes.length));
			String cipheredText =cipherMsg(text, originalKey,iv);
			System.out.println("CIPHER MSG : " + cipheredText );
			System.out.println("PLAITEXT MSG: " + decipherMsg(cipheredText, originalKey,iv));
			preHash(data);
			byte[] signature = signMessageRSA(data, privateKey);
			System.out.println("Signature verifies: " + verifySignedMessagedRSA(data, signature, publicKey));

		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
