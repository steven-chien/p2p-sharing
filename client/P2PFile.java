import java.io.*;
import java.util.*;
import java.lang.*;
import java.security.*;

class P2PFile extends FSNode
{
	String path;
	File file;
	long totalSize;
	long blockCount;
	int lastBlockSize;
	byte[][] hash;

	public P2PFile(String path) throws Exception {
		this.path = path;
		this.file = new File(path);
		this.totalSize = this.file.length();

		long modSize = (totalSize % (long)Math.pow(2, 18));
		blockCount = (totalSize - modSize) / (long)Math.pow(2, 18);
		if(modSize!=0) {
			this.lastBlockSize = (int) modSize;
			blockCount++;
		}

		System.err.println("path:"+this.path+"; totalSize: "+totalSize+"; blockCount: "+blockCount+"; last block size: "+this.lastBlockSize);

		FileInputStream stream = new FileInputStream(this.path);
		MessageDigest hashSum = MessageDigest.getInstance("SHA-256");
		hash = new byte[(int)blockCount][];
		for(int i=0; i<blockCount; i++) {
			int readCount = (int)Math.pow(2, 18);
            
            if (lastBlockSize != 0 && i+1 == blockCount) {
                System.err.println("This is the last block:");
                readCount = lastBlockSize;
            }
            
            int blockSize = readCount;
            
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int read;
			while(readCount>0) {
                byte[] buffer = new byte[readCount];
                System.err.println(" offset: "+(blockSize-readCount)+" Readcunt: "+readCount);
				read = stream.read(buffer, (blockSize-readCount), readCount);
				outputStream.write(buffer, 0, read);
				readCount -= read;
			}
			hash[i] = hashSum.digest(outputStream.toByteArray());
			System.err.println("hash "+i+": "+bytesToHex(hash[i]));
		}
	}
    
    /*
     * Used for debugging; This method is stolen from Stack Overflow
     */
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
}
