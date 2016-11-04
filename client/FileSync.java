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

	public synchronized void update(String data) {
		try {
			metaData = new JSONObject(data);
			System.err.println("Updated metadata from other peer");
		} catch (Exception e){
			System.err.println("Failed to decode data from peer: " + e.toString());
		}
	}

	public synchronized void newPeer(Socket socket) {
		peers.add(new Peer(socket, this));
	}

}
