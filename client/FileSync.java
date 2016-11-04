import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import org.json.*;

class FileSync
{
	Tracker tracker;
	private ArrayList<Peer> peers;
	private JSONObject metaData;
	public Publish publish;

	public FileSync(Tracker tracker) {
		this(tracker, null);
	}

	public FileSync(Tracker tracker, Publish publish) {
		this.tracker = tracker;
		this.publish = publish;

		peers = new ArrayList<Peer>();

		JSONArray peerList = tracker.getPeers().getJSONArray("peers");
		System.err.println("peer list: "+peerList.toString());
		System.err.println();

		for(int i=0; i<peerList.length(); i++) {
			JSONObject peerObj = peerList.getJSONObject(i);
			try {
				Socket socket = new Socket(peerObj.getString("ip"), peerObj.getInt("port"));
				newPeer(socket);
			} catch(Exception e) {
				System.out.println("Unable to connect to peer, exception: "+e.toString());
			}
		}
	}

	public synchronized void sendFile(PrintWriter out, BufferedReader in, String path, int blockCount) throws Exception {
		File file = new File(path);
		PrintWriter pw = new PrintWriter(file);
		try {
			StringBuilder builder = new StringBuilder();
	
			for(int i=0; i<blockCount; i++) {
				out.println("getblock");
				out.println(path);
				out.println(i);
				builder.append(in.readLine());
			}
	
			pw.print(builder.toString());
		} catch(Exception e) {
			System.err.println("Exception when send file: " + e.toString());
		} finally {
			pw.close();
		}
	}

	public synchronized boolean update(String data) {
		try {
			if(metaData==null || !metaData.toString().equals(data)) {
				metaData = new JSONObject(data);

				JSONObject obj = metaData.getJSONObject("metadata");
				JSONArray folderList = obj.getJSONArray("folders");

				//for(JSONString folder : folderList) {
				for(int i=0; i<folderList.length(); i++) {
					(new File(folderList.getJSONObject(i).toString())).mkdir();
				}

				System.err.println("Updated metadata from other peer");
				return true;
			}
			else {
				System.err.println("got metadata from other peer but already have the latest version will not update");
			}
		} catch (Exception e){
			System.err.println("Failed to decode data from peer: " + e.toString());
		}
		return false;
	}

	public synchronized ArrayList<Peer> getPeers() {
		return peers;
	}

	public synchronized void newPeer(Socket socket) {
		peers.add(new Peer(socket, this));
	}

}
