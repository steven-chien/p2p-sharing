import java.io.*;
import java.util.*;
import java.lang.*;
import java.security.*;
import org.json.*;

class Publish
{
	private P2PFolder folder;
	private JSONObject metaData;

	private PublicKey publicKey;
	private PrivateKey privateKey;

	private Signature sig;
	private byte[] signatureBytes;

	private long revision;

	public Publish(String absolutePath) throws Exception {
		this(absolutePath, 0);
	}

	public Publish(String absolutePath, long revision) throws Exception {
		/* create root folder object */
		folder = new P2PFolder(absolutePath);
		revision = revision;
		metaData = folder.toJSON();

		/* get key pairs */
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();

		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();

		/* sign meta data */
		sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privateKey);
		sig.update(hexStringToByteArray(metaData.toString()));
		signatureBytes = sig.sign();

		System.out.println("Singature:" + bytesToHex(signatureBytes));
	}

	public String getMetaData() {
		JSONObject json = new JSONObject();
		json.put("metadata", metaData);
		json.put("signature", bytesToHex(signatureBytes));
		return json.toString();
	}

	final protected char[] hexArray = "0123456789ABCDEF".toCharArray();
	public String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/*
	* Used for debugging; This method is stolen from Stack Overflow
	*/
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
				+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
}
