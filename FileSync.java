import java.io.*;
import java.util.*;
import java.lang.*;

class FileSync
{
	Tracker tracker;
	private ArrayList<Peer> peers;

	public FileSync(Tracker tracker) {
		this.tracker = tracker;
		peers = new ArrayList<Peer>();
	}
}
