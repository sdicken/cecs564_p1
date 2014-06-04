package cecs564_p1;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Driver 
{
	public static void main(String [] args)
	{
		try {
			// convert List of Strings into List of words
			List<String> words = Utils.convertLinesToWords(Utils.readLinesOfFile("test1.txt"));
			Map<String, Integer> keys = Utils.readKeyFile("keyfile");
			String ciphertext = Utils.encrypt(words, keys.get(Utils.A_KEY), keys.get(Utils.K_KEY));
			Utils.decrypt(ciphertext, keys.get(Utils.A_KEY), keys.get(Utils.K_KEY), false);
			ciphertext = Utils.readLinesOfFile("test3.txt").get(0);
			Map<String, Integer> senderKeys = Utils.attack(ciphertext, true);
			System.out.println("a: " + senderKeys.get(Utils.A_KEY));
			System.out.println("k: " + senderKeys.get(Utils.K_KEY));
			Utils.decrypt(ciphertext, senderKeys.get(Utils.A_KEY), senderKeys.get(Utils.K_KEY), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
