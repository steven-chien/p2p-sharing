import java.io.*;
import java.util.*;
import java.net.*;
import org.json.*;

class Tracker
{
	private JSONObject peers;

	public Tracker(String trackerUrl, int clientPort, String infoHash, String peerId, String key) {
		String registration = "https://" + trackerUrl + "/tracker.php?info_hash=" + infoHash + "&port=" + clientPort + "&peer_id=" + peerId + "&key=" + key;
		//String registration = "https://caprioli.se/tracker.php?port=169&info_hash=01234567890123456789012345678912&peer_id=12134567890323456789012345678912&key=01234567890123456789012345678912";

		/* register against tracker server*/
		try {
			URL requestUrl = new URL(registration);
			HttpURLConnection conn = (HttpURLConnection)requestUrl.openConnection();
			conn.setRequestMethod("GET");

			int responseCode = conn.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer response = new StringBuffer();
				response.append(in.readLine());
				in.close();

				System.out.println(response.toString());

				/* parse json */
				peers = new JSONObject(response.toString());
			} else {
				System.out.println("GET request not worked");
				System.exit(1);
			}
		}
		catch(Exception ex) {
			System.err.println("Exception caught: "+ex.toString());
			System.exit(1);
		}
	}

	JSONObject getPeers() {
		return peers;
	}
}
