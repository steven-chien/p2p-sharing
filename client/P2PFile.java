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
	long lastBlockSize;
	byte[][] hash;

	public P2PFile(String path) throws Exception {
		this.path = path;
		this.file = new File(path);
		this.totalSize = this.file.length();

		long modSize = (totalSize % (long)Math.pow(2, 18));
		blockCount = (totalSize - modSize) / (long)Math.pow(2, 18);
		if(modSize!=0) {
			this.lastBlockSize = modSize;
			blockCount++;
		}

		System.err.println("path:"+this.path+"; totalSize: "+totalSize+"; blockCount: "+blockCount+"; last block size: "+this.lastBlockSize);

		FileInputStream stream = new FileInputStream(this.path);
		MessageDigest hashSum = MessageDigest.getInstance("SHA-256");
		hash = new byte[(int)blockCount][];
		for(int i=0; i<blockCount; i++) {
			int readCount = (int)Math.pow(2, 18);
			byte[] buffer = new byte[readCount];
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int read;
			while(readCount>0) {
				read = stream.read(buffer, (((int)Math.pow(2, 18))-readCount)+i, readCount);
				System.err.println(" offset: "+(((int)Math.pow(2, 18))+i,readCount));
				outputStream.write(buffer, 0, read);
				readCount -= read;
			}
			hash[i] = hashSum.digest(outputStream.toByteArray());
			System.err.println("hash "+i+": "+Arrays.toString(hash[i]));
		}
	}
}
