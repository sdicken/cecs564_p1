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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Utils 
{
	private static final Integer X0 = 0;
	private static final int LOWERCASE_ASCII_A = 97;
	private static final Integer UPPERCASE_ASCII_A = 65;
	public static final String A_KEY = "aKey";
	public static final String K_KEY = "kKey";
	private static final String [] commonTwoLetterWords = {"of", "to", "in", "it", "is", 
		"be", "as", "at", "so", "we", "he", "by", "or", "on", "do", "if", "me", "my", 
		"up", "an", "go", "no", "us", "am"};
	
	private static final String [] commonThreeLetterWords = {"the", "and", "for", "are", "but",
		"not", "you", "all", "any", "can", "had", "her", "was", "one", "our", "out", "day", 
		"get", "has", "him", "his", "how", "man"};
	
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
	
	public static String decrypt(String ciphertext, Integer aKey, Integer kKey, boolean testMode)
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
		if(!testMode)
			writeToFile("plaintext.txt", plaintext);
		return plaintext;
	}
	
	public static Map<String, Integer> attack(String ciphertext, boolean interactiveMode)
	{
		List<Map<Integer,Map<Integer,Integer>>> top5 = new ArrayList<Map<Integer,Map<Integer,Integer>>>();
		Integer minimumSequencesRecognized = Integer.MAX_VALUE;
		Integer maximumSequencesRecognized = Integer.MIN_VALUE;
		for(int i = 1; i < 26; i++)	// loop over aKey space
		{
			for(int j = 1; j < 26; j++)	// loop over kKey space
			{
				String possiblePlaintext = decrypt(ciphertext, i, j, true);
				Integer sequencesRecognized = performEnglishAnalysis(possiblePlaintext);
				
				if(top5.size() < 5)
				{
					Map<Integer,Integer> kKeyToSequencesRecognizedMap = Collections.singletonMap(j, sequencesRecognized);
					Map<Integer,Map<Integer,Integer>> aKeyTokKeyMap = Collections.singletonMap(i, kKeyToSequencesRecognizedMap);
					top5.add(aKeyTokKeyMap);
				}
				else	// remove one with lowest # of sequences recognized
				{
					if(sequencesRecognized <= minimumSequencesRecognized) // weed these out right away, don't want to search top 5 needlessly
					{
						continue;
					}
					else
					{
						int minimumIndex = 0;
						int minimumValue = Integer.MAX_VALUE;
						for(int k = 0; k < top5.size(); k++)
						{
							Map<Integer,Map<Integer,Integer>> current = top5.get(k);
							for(Entry<Integer,Map<Integer,Integer>> entry1 : current.entrySet())
							{
								Map<Integer,Integer> current2 = entry1.getValue();
								for(Entry<Integer,Integer> entry2 : current2.entrySet())
								{
									Integer val = entry2.getValue();
									if(val < minimumValue)
									{
										minimumIndex = k;
										minimumValue = val;
									}
								}
							}
						}
						top5.remove(minimumIndex);
						Map<Integer,Integer> kKeyToSequencesRecognizedMap = Collections.singletonMap(j, sequencesRecognized);
						Map<Integer,Map<Integer,Integer>> aKeyTokKeyMap = Collections.singletonMap(i, kKeyToSequencesRecognizedMap);
						top5.add(aKeyTokKeyMap);
					}
				}
				
				if(sequencesRecognized > maximumSequencesRecognized)
				{
					maximumSequencesRecognized = sequencesRecognized;
				}
				else if(sequencesRecognized < minimumSequencesRecognized)
				{
					minimumSequencesRecognized = sequencesRecognized;
				}
			}
		}
		Map<String, Integer> result = chooseFromDecryptionOptions(ciphertext, top5, interactiveMode);
		return result;
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
			for(int j = 0; j < line.length(); j++)
			{
				int asciiByteValue = (int) line.charAt(j);
				if((asciiByteValue >= 65 && asciiByteValue <= 90) || (asciiByteValue >= 97 && asciiByteValue <= 122))
				{
					output.add(String.valueOf(line.charAt(j)));
				}
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
	
	public static Integer performEnglishAnalysis(String decryptedText)
	{
		Integer result = 0;
		List<String> commonTwoWordList = Arrays.asList(commonTwoLetterWords);
		List<String> commonThreeWordList = Arrays.asList(commonThreeLetterWords);
		for(int i = 0; i < commonTwoWordList.size(); i++)
		{
			if(decryptedText.contains(commonTwoWordList.get(i)))
			{
				result++;
			}
		}
		for(int i = 0; i < commonThreeWordList.size(); i++)
		{
			if(decryptedText.contains(commonThreeWordList.get(i)))
			{
				result++;
			}
		}
		return result;
	}
	
	private static Map<String, Integer> chooseFromDecryptionOptions(String ciphertext,
			List<Map<Integer, Map<Integer, Integer>>> top5, boolean interactiveMode) 
	{
		Map<String, Integer> result = new HashMap<String, Integer>();
		for(int k = 0; k < top5.size(); k++)
		{
			Map<Integer,Map<Integer,Integer>> current = top5.get(k);
			for(Entry<Integer,Map<Integer,Integer>> entry1 : current.entrySet())
			{
				Integer aKey = entry1.getKey();
				Map<Integer,Integer> current2 = entry1.getValue();
				for(Entry<Integer,Integer> entry2 : current2.entrySet())
				{
					Integer kKey = entry2.getKey();
					System.out.println(k + ") " + decrypt(ciphertext, aKey, kKey, true).substring(0, 20));
				}
			}
		}
		Map<Integer,Map<Integer,Integer>> choice = null;
		if(interactiveMode)
		{
			System.out.print("Choose most correct looking option: ");
			Scanner scanner = new Scanner(System.in);
			int chosenOption = scanner.nextInt();
			scanner.close();
			choice = top5.get(chosenOption);
		}
		else
		{
			choice = top5.get(0);
		}
		Integer aKey = 0;
		Integer kKey = 0;
		for(Entry<Integer,Map<Integer,Integer>> entry1 : choice.entrySet())
		{
			aKey = entry1.getKey();
			Map<Integer,Integer> current2 = entry1.getValue();
			for(Entry<Integer,Integer> entry2 : current2.entrySet())
			{
				kKey = entry2.getKey();
			}
			
		}
		result.put(A_KEY, aKey);
		result.put(K_KEY, kKey);
		return result;
	}
}
