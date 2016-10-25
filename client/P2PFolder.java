import java.io.*;
import java.util.*;
import java.lang.*;
import org.json.*;

class P2PFolder extends FSNode
{
	private int fileCount;
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
			//System.err.println(f.getName());
			if(f.isDirectory()) fileList.add(new P2PFolder(f));
			else if(f.isFile()) fileList.add(new P2PFile(f));
			//else System.err.println("Neither a file or a dir...");
		}
	}
    
    @Override
    public JSONObject toJSON() {
        JSONArray files = new JSONArray();
        JSONArray folders = new JSONArray();
        return this.toJSON(folders, files);
    }

    @Override
    public JSONObject toJSON(JSONArray folders, JSONArray files) {
        
        folders.put(this.path);
        
        for(FSNode fsNode : fileList) {
            if (fsNode instanceof P2PFolder) {
                fsNode.toJSON(folders, files);
            }
            
            if (fsNode instanceof P2PFile) {
                files.put(fsNode.toJSON());
            }
            
        }
        
	    JSONObject ret = new JSONObject();
        ret.put("files", files);
        ret.put("folders", folders);
	    return ret;
    }
 
}
