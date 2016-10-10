import java.io.*;
import java.util.*;

public class Sharing
{
	public static void main(String[] argc) {
		Tracker tracker = new Tracker("caprioli.se", 169, "01234567890123456789012345678912", "12134567890323456789012345678912", "01234567890123456789012345678912");
		FileSync fileSync = new FileSync(tracker);
		
		System.out.println(tracker.getPeers().toString());
	}
}
