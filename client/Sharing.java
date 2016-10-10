import java.io.*;
import java.util.*;

public class Sharing
{
	public static void main(String[] argc) {
		Tracker tracker = new Tracker("caprioli.se", 1337, "01234567890123456789012345678912", "12134567890323436789012345678914", "01234567890123456789012345678912");
		FileSync fileSync = new FileSync(tracker);
		(new Thread(new Listener(8888, fileSync))).start();
		
		System.out.println(tracker.getPeers().toString());
	}
}
