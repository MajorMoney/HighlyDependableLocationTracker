package pt.ist.stdf.HighlyDependableTracker.CryptoUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CryptoUtils {

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		return kp;
	}

	public static SecretKey generateKeyAES() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		// System.out.println("AES KEY: " + new String(key.getEncoded(), 0,
		// key.getEncoded().length));
		return key;
	}

	public static void generateStrongSecureRandom(byte[] nextBytes) throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstanceStrong();
		sr.nextBytes(nextBytes);
	}

	public static IvParameterSpec generateIv() throws NoSuchAlgorithmException {
		byte[] iv = new byte[16];
		generateStrongSecureRandom(iv);
		// System.out.println("IV: " + new String(iv, 0, iv.length));
		return new IvParameterSpec(iv);
	}

	public static void preHash(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
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

	public static byte[] cipherKey(byte[] key, PublicKey pk) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		c.init(Cipher.ENCRYPT_MODE, pk);
		return c.doFinal(key);
	}

	public static byte[] decipherKey(byte[] key, PrivateKey pk) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, pk);
		return cipher.doFinal(key);
	}
	

	public static String getIvForMessage(IvParameterSpec iv) throws NoSuchAlgorithmException {
		String ivs = Base64.getEncoder().encodeToString(iv.getIV());
		return ivs;
		
	}
	
	public static String encryptJsonObjectToString(JsonObject json,SecretKey key,IvParameterSpec iv) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		String encryptedString = cipherMsg(json.toString(), key, iv );
		return encryptedString;

	}
	public static JsonObject decryptStringToJsonObject(String s,SecretKey key,IvParameterSpec iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		String decrypted = CryptoUtils.decipherMsg(s, key, iv);
		JsonObject json = new Gson().fromJson(decrypted, JsonObject.class);
		return json;
		
	}
	public static IvParameterSpec getIvFromMessage(String iv) throws NoSuchAlgorithmException {
		byte[] ivb = Base64.getDecoder().decode(iv);
		IvParameterSpec trueIv = new IvParameterSpec(ivb);
		return trueIv;
	}
	
	public static String cipherMsg(String msg, SecretKey key, IvParameterSpec iv)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(msg.getBytes());
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decipherMsg(String cipher, SecretKey key, IvParameterSpec iv)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] plainText = c.doFinal(Base64.getDecoder().decode(cipher));
		return new String(plainText);
	}

	public static PrivateKey getPrivateKeyFromString(String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			return priKey;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getKeyToString(byte[] encoded) {
		return java.util.Base64.getEncoder().encodeToString(encoded);
	}

	public static PublicKey getPublicKeyFromString(String pub) {
		try {
			//pub.replace('\n', ' ');
			X509EncodedKeySpec publicz = new X509EncodedKeySpec(Base64.getDecoder().decode(pub));
			KeyFactory keyf;
			keyf = KeyFactory.getInstance("RSA");

			PublicKey pubKey = keyf.generatePublic(publicz);
			return pubKey;

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sign(String samlResponseString, PrivateKey pkey)
            throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException, UnsupportedEncodingException {
        String signedString = null;
        Signature signature = Signature.getInstance("SHA512withRSA");
        signature.initSign(pkey);
        signature.update(samlResponseString.getBytes());
        byte[] signatureBytes = signature.sign();
        byte[] encryptedByteValue = Base64.getEncoder().encode(signatureBytes);
        signedString = new String(encryptedByteValue, "UTF-8");
        return signedString;
    }

	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
	    Signature publicSignature = Signature.getInstance("SHA512withRSA");
	    publicSignature.initVerify(publicKey);
	    publicSignature.update(plainText.getBytes("UTF-8"));

	    byte[] signatureBytes = Base64.getDecoder().decode(signature);

	    return publicSignature.verify(signatureBytes);
	}
	public static void main(String args[]) {
		try {
			byte[] data = "Data to be signed".getBytes();
			KeyPair kp = generateKeyPair();
			System.out.println(kp.getPrivate());
			System.out.println(kp.getPublic());
			SecretKey aes = generateKeyAES();
			PublicKey publicKey = kp.getPublic();
			PrivateKey privateKey = kp.getPrivate();
			IvParameterSpec iv = generateIv();

			String text = new String(data, 0, data.length);
			byte[] encAes = cipherKey(aes.getEncoded(), publicKey);
			System.out.println("CIPHER AES : " + new String(encAes, 0, encAes.length) + "\n");
			byte[] decAes = decipherKey(encAes, privateKey);
			SecretKey originalKey = new SecretKeySpec(decAes, 0, decAes.length, "AES");
			System.out.println("PLAITEXT AES : " + new String(decAes, 0, decAes.length));
			String cipheredText = cipherMsg(text, originalKey, iv);
			System.out.println("CIPHER MSG : " + cipheredText);
			System.out.println("PLAITEXT MSG: " + decipherMsg(cipheredText, originalKey, iv));
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
