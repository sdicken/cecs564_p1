package cecs564_p1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils 
{
	private static final Integer X0 = 0;
	private static final Integer Y0 = 0;
	private static final int LOWERCASE_ASCII_A = 97;
	private static final Integer UPPERCASE_ASCII_A = 65;
	
	public static String encrypt(List<String> words, Integer aKey, Integer kKey)
	{
		List<List<Integer>> plaintextInZ26 = removeASCIIEncoding(convertWordsToASCIIDecimal(convertUpperToLower(words)));
		StringBuilder sb = new StringBuilder();
		int recursionCounter = 0;
		for(int i = 0; i < plaintextInZ26.size(); i++)
		{
			List<Integer> innerList = plaintextInZ26.get(i);
			for(int j = 0; j < innerList.size(); j++)
			{
				Integer z26Value = innerList.get(j);
				Integer encipheredValue = new Integer(0);
				if(recursionCounter == 0)
				{
					encipheredValue = (z26Value + aKey*X0 + kKey) % 26;
				}
				else
				{
					Integer previousZ26Value = innerList.get(j-1);
					encipheredValue = (z26Value + aKey*previousZ26Value + kKey) % 26;
				}
				encipheredValue += UPPERCASE_ASCII_A; // by convention for ciphertext
				char ciphertextCharacter = (char) encipheredValue.byteValue();
				sb.append(ciphertextCharacter);
				recursionCounter++;
			}
		}
		return sb.toString();
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
			decipheredValue += LOWERCASE_ASCII_A;
			char plaintextCharacter = (char) decipheredValue.byteValue();
			sb.append(plaintextCharacter);
			recursionCounter++;
		}
		return sb.toString();
	}
	
	public static void attack()
	{
		
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
	
	public static List<List<Integer>> removeASCIIEncoding(List<List<Integer>> asciiEncodedLetters)
	{
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for(int i = 0; i < asciiEncodedLetters.size(); i++)
		{
			List<Integer> innerList = asciiEncodedLetters.get(i);
			for(int j = 0; j < innerList.size(); j++)
			{
				Integer oldElement = innerList.get(j);
				Integer shift = Integer.valueOf(LOWERCASE_ASCII_A);
				innerList.set(j, oldElement - shift);
			}
			result.add(innerList);
		}
		return result;
	}
}
