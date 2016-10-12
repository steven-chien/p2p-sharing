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
		this.path = file.getAbsolutePath();;
		File folder = new File(path);
		
		fileList = new ArrayList<FSNode>(folder.listFiles().length);
		for(File f : folder.listFiles()) {
			System.err.println(f.getName());
			if(f.isDirectory()) fileList.add(new P2PFolder(f));
			else if(f.isFile()) fileList.add(new P2PFile(f));
			else System.err.println("Neither a file or a dir...");
		}
	}
}
