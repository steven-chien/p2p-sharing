import java.io.*;
import java.util.*;
import java.lang.*;
import java.security.*;
import org.json.*;

class P2PFile extends FSNode
{
	File file;
	long totalSize;
	long blockCount;
	int lastBlockSize;
	byte[][] hash;
	static ArrayList<P2PFile> files = new ArrayList<P2PFile>();

    public P2PFile(String path) throws Exception {
        this(new File(path));
    }

	public P2PFile(File file) throws Exception {
        if (!file.exists()) throw new Exception("This file does not exist on disk");
        if (!file.isFile()) throw new Exception("This file is not a file, maybe it is a folder?");
        
        this.file = file;
        this.path = file.getPath();
		this.totalSize = this.file.length();

		long modSize = (totalSize % (long)Math.pow(2, 18));
		blockCount = (totalSize - modSize) / (long)Math.pow(2, 18);
		if(modSize!=0) {
			this.lastBlockSize = (int) modSize;
			blockCount++;
		}

		//System.err.println("path:"+this.path+"; totalSize: "+totalSize+"; blockCount: "+blockCount+"; last block size: "+this.lastBlockSize);

		FileInputStream stream = new FileInputStream(this.path);
		MessageDigest hashSum = MessageDigest.getInstance("SHA-256");
		hash = new byte[(int)blockCount][];
		for(int i=0; i<blockCount; i++) {
			int readCount = (int)Math.pow(2, 18);
            
            if (lastBlockSize != 0 && i+1 == blockCount) {
                //System.err.println("This is the last block:");
                readCount = lastBlockSize;
            }
            
            int blockSize = readCount;
            
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int read;
			while(readCount>0) {
                byte[] buffer = new byte[readCount];
                //System.err.println(" offset: "+(blockSize-readCount)+" Readcunt: "+readCount);
				read = stream.read(buffer, (blockSize-readCount), readCount);
				outputStream.write(buffer, 0, read);
				readCount -= read;
			}
			hash[i] = hashSum.digest(outputStream.toByteArray());
			//System.err.println("hash "+i+": "+bytesToHex(hash[i]));
		}
        stream.close();
        files.add(this);
	}
    
    public byte[] getBlock(int block) throws Exception {
        FileInputStream stream = new FileInputStream(this.path);
        int readCount = (int)Math.pow(2, 18);
        stream.skip((readCount * ((long) block)));
        if (block+1 == blockCount) readCount = lastBlockSize;
        
        byte[] buffer = new byte[readCount];
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        int read;
        while(readCount>0) {
            buffer = new byte[readCount];
            read = stream.read(buffer, 0, readCount);
            outputStream.write(buffer, 0, read);
            readCount -= read;
        }
        
        stream.close();
        return outputStream.toByteArray();
    }
    
    public byte[] getBlock(byte[] getHash) throws Exception {
        int block = 0;
        for (byte[] str : hash) {
            //System.err.println("Comparing "+bytesToHex(str));
            //System.err.println("to        "+bytesToHex(getHash)+"\n");
            if (Arrays.equals(str, getHash)) {
                return getBlock(block);
            }
            ++block;
        }
        return null;
    }
    
    public byte[][] getHashes() {
        return hash;
    }

    @Override
    public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    json.put("path", this.path);
	    json.put("size", this.totalSize);
	    JSONArray fileHashes = new JSONArray();
	    for(byte[] blockHash : hash) {
		    fileHashes.put(bytesToHex(blockHash));
	    }
	    json.put("hashes", fileHashes);

	    return json;
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
