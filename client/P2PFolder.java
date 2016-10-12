import java.io.*;
import java.util.*;
import java.lang.*;

class P2PFolder extends FSNode
{
	private int fileCount;
	private String path;
	private ArrayList<FSNode> fileList;

	public P2PFolder(String path) throws Exception {
		this.path = path;
		fileCount = Files.list(Paths.get(this.path)).count();

		fileList = new ArrayList<FSNode>(fileCount);
	}
}
