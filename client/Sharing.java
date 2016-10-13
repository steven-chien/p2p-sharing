import java.io.*;
import java.util.*;
import java.security.*;

public class Sharing
{
	public static void main(String[] args) {

		try {
			//P2PFile file = new P2PFile("./test");
	//            System.err.println(bytesToHex(file.getBlock(hexStringToByteArray("C035974989C68B7829A393046ECC33E35C09370FA0A26F969CF93ED2D98DB1C6"))));
	    		//P2PFolder folder = new P2PFolder("./testFolder");
			//System.err.println(folder.toJSON().toString(2));
			Publish f = new Publish("./testFolder");
			System.err.println(f.getMetaData());
            
            
		}
		catch(Exception ex) {
			System.err.println("Exception: "+ex.toString());
			System.exit(1);
		}

		Tracker tracker = new Tracker("caprioli.se", 1337, "01234567890123456789012345678912", "12134567810323436789012345678914", "01234567890123456789012345678912");
		FileSync fileSync = new FileSync(tracker);
		(new Thread(new Listener(1337, fileSync))).start();

		System.out.println(tracker.getPeers().toString());
	}
    
    /*
     * Used for debugging; This method is stolen from Stack Overflow
     */
    final static protected char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
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
