package cecs564_p1;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils 
{
	private static final Integer X0 = 0;
	private static final int LOWERCASE_ASCII_A = 97;
	private static final Integer UPPERCASE_ASCII_A = 65;
	public static final String A_KEY = "aKey";
	public static final String K_KEY = "kKey";
	
	public static String encrypt(List<String> words, Integer aKey, Integer kKey)
	{
		List<Integer> plaintextInZ26 = removeASCIIEncoding(convertWordsToASCIIDecimal(convertUpperToLower(words)));
		StringBuilder sb = new StringBuilder();
		int recursionCounter = 0;
		Integer previousPlaintextZ26Value = new Integer(0);
		for(int i = 0; i < plaintextInZ26.size(); i++)
		{
			Integer z26Value = plaintextInZ26.get(i);

			Integer encipheredValue = new Integer(0);
			if(recursionCounter == 0)
			{
				encipheredValue = (z26Value + aKey*X0 + kKey) % 26;
			}
			else
			{
				encipheredValue = (z26Value + aKey*previousPlaintextZ26Value + kKey) % 26;
			}
			previousPlaintextZ26Value = z26Value;
			encipheredValue += UPPERCASE_ASCII_A; // by convention for ciphertext
			char ciphertextCharacter = (char) encipheredValue.byteValue();
			sb.append(ciphertextCharacter);
			recursionCounter++;
			
		}
		String ciphertext = sb.toString();
		writeToFile("ciphertext.txt", ciphertext);
		return ciphertext;
	}
	
	public static String decrypt(String ciphertext, Integer aKey, Integer kKey)
	{
		char[] ciphertextCharacters = ciphertext.toCharArray();
		int recursionCounter = 0;
		StringBuilder sb = new StringBuilder();
		Integer previousDecipheredValue = new Integer(0);
		for(int i = 0; i < ciphertextCharacters.length; i++)
		{
			char ciphertextCharacter = ciphertextCharacters[i];			
			Integer asciiValue = (int) ciphertextCharacter;
			Integer ciphertextInZ26 = asciiValue - UPPERCASE_ASCII_A;
			Integer decipheredValue = new Integer(0);
			
			if(recursionCounter == 0)
			{
				decipheredValue = (ciphertextInZ26 - aKey*X0 - kKey) % 26;
			}
			else
			{
				decipheredValue = (ciphertextInZ26 - aKey*previousDecipheredValue - kKey) % 26;
			}
			// borrowed from https://stackoverflow.com/questions/4403542/how-does-java-do-modulus-calculations-with-negative-numbers
			if (decipheredValue < 0)
			{
				decipheredValue += 26;
			}
			previousDecipheredValue = decipheredValue;	// capture value before adding ASCII encoding
			decipheredValue += LOWERCASE_ASCII_A;	// by convention for plaintext
			char plaintextCharacter = (char) decipheredValue.byteValue();
			sb.append(plaintextCharacter);
			recursionCounter++;
		}
		String plaintext = sb.toString();
		writeToFile("plaintext.txt", plaintext);
		return plaintext;
	}
	
	public static String attack(String ciphertext)
	{
		for(int i = 1; i < 26; i++)
		{
			for(int j = 1; j < 26; j++)
			{
				System.out.println(decrypt(ciphertext, i, j));
			}
		}
		return null;
	}
	
	// -- BEGIN PREVIOUSLY TESTED METHODS --
	public static List<String> readLinesOfFile(String path) throws IOException
	{
		Path pathToFile = Paths.get(path);
		return Files.readAllLines(pathToFile, StandardCharsets.US_ASCII);
	}
	
	public static List<String> convertLinesToWords(List<String> lines)
	{
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			List<String> splits = Arrays.asList(line.split(" "));
			for(int j = 0; j < splits.size(); j++)
			{
				String split = splits.get(j);
				if(split.equals(""))
					continue;
				output.add(splits.get(j));
			}
		}
		return output;
	}
	
	public static List<String> convertUpperToLower(List<String> words)
	{
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < words.size(); i++)
		{
			String line = words.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append(line.toLowerCase());
			output.add(sb.toString());
		}
		return output;
	}
	
	// -- END PREVIOUSLY TESTED METHODS --
	
	public static List<List<Integer>> convertWordsToASCIIDecimal(List<String> words)
	{
		List<List<Integer>> output = new ArrayList<List<Integer>>();
		for(int i = 0; i < words.size(); i++)
		{
			String line = words.get(i);
			List<Integer> innerList = new ArrayList<Integer>();
			for(int j = 0; j < line.length(); j++)
			{
				char itemInLine = line.charAt(j);
				int charAsDecimal = (int) itemInLine;
				innerList.add(Integer.valueOf(charAsDecimal));
			}
			output.add(innerList);
		}
		return output;
	}
	
	public static List<Integer> removeASCIIEncoding(List<List<Integer>> asciiEncodedLetters)
	{
		List<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < asciiEncodedLetters.size(); i++)
		{
			List<Integer> innerList = asciiEncodedLetters.get(i);
			for(int j = 0; j < innerList.size(); j++)
			{
				Integer oldElement = innerList.get(j);
				Integer shift = Integer.valueOf(LOWERCASE_ASCII_A);
				result.add(oldElement - shift);
			}
		}
		return result;
	}
	
	public static Map<String, Integer> readKeyFile(String path) throws IOException
	{
		Map<String, Integer> result = new HashMap<String, Integer>();
		List<String> lines = readLinesOfFile(path);
		for(int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			if(line.startsWith(A_KEY))
			{
				result.put(A_KEY, Integer.valueOf(line.split("=")[1]));
			}
			else if(line.startsWith(K_KEY))
			{
				result.put(K_KEY, Integer.valueOf(line.split("=")[1]));
			}
		}
		return result;
	}
	
	private static void writeToFile(String filename, String fileContents)
	{
		Writer writer = null;
		
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.US_ASCII));
			writer.write(fileContents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
