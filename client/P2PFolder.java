import java.io.*;
import java.util.*;
import java.lang.*;

class P2PFolder extends FSNode
{
	private int fileCount;
	private String path;
	private ArrayList<FSNode> fileList;

	public P2PFolder(String path) throws Exception {
		this(new File(path));
	}

	public P2PFolder(File file) throws Exception {
        	if (!file.exists()) throw new Exception("This file does not exist on disk");
	        if (!file.isDirectory()) throw new Exception("This is not a folder.");

		this.path = file.getPath();
		File folder = new File(path);
		
		fileList = new ArrayList<FSNode>(folder.listFiles().length);
		for(File f : folder.listFiles()) {
			System.err.println(f.getName());
			if(f.isDirectory()) fileList.add(new P2PFolder(f));
			else if(f.isFile()) fileList.add(new P2PFile(f));
			else System.err.println("Neither a file or a dir...");
		}
	}

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
 
}
