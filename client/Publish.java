import java.io.*;
import java.util.*;
import java.lang.*;
import java.security.*;
import java.security.spec.*;
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
		System.err.println("publishing");
		/* create root folder object */
		folder = new P2PFolder(absolutePath);
		this.revision = revision;
		metaData = folder.toJSON();
        
        if (!(new File(folder.path+".json").exists())) {
            /* get key pairs */
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            revision = 0;

            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
            signatureBytes = signData(hexStringToByteArray(metaData.toString()), privateKey);
            saveConfig();
        } else {
            loadConfig();
        }
        
        
        
	}

	private byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	private boolean verifyData(byte[] data, byte[] sigBytes, PublicKey publicKey) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(sigBytes);
	}

	private void saveConfig() throws Exception {
		JSONObject config = new JSONObject();
		config.put("publicKey", savePublicKey(publicKey));
		config.put("privateKey", savePrivateKey(privateKey));
        config.put("revision", revision);
        PrintWriter pw = new PrintWriter(folder.path + ".json"); 
        pw.print(config.toString());
        pw.close();
	}
    
    private void loadConfig() throws Exception {
        JSONObject json = new JSONObject(readFile(folder.path+".json"));
        privateKey = loadPrivateKey(json.getString("privateKey"));
        publicKey = loadPublicKey(json.getString("publicKey"));
        revision = json.getLong("revision");
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
    
    /*
     * Stolen from Stack Overflow
     */
    public String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
    
    public PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = hexStringToByteArray(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }


    public PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = hexStringToByteArray(stored);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    public String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = bytesToHex(packed);

        Arrays.fill(packed, (byte) 0);
        return key64;
    }

    public String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        return bytesToHex(spec.getEncoded());
    }
}
