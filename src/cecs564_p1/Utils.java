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
	private static final int X0 = 0;
	private static final int Y0 = 0;
	
	public static void encrypt()
	{
		
	}
	
	public static void decrypt()
	{
		
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
	
	public static List<String> convertWordsToASCIIDecimal(List<String> words)
	{
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < words.size(); i++)
		{
			String line = words.get(i);
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for(int j = 0; j < line.length(); j++)
			{
				if(first)
					first = false;
				else
					sb.append(" ");
				char itemInLine = line.charAt(j);
				int charAsDecimal = (int) itemInLine;
				sb.append(charAsDecimal);
			}
			output.add(sb.toString());
		}
		return output;
	}
	
	// -- END PREVIOUSLY TESTED METHODS --
}
