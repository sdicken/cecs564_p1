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
			Utils.decrypt(ciphertext, keys.get(Utils.A_KEY), keys.get(Utils.K_KEY));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
