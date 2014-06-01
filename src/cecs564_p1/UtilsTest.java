package cecs564_p1;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UtilsTest 
{
	@Test
	public void testRemoveASCII()
	{
		List<List<Integer>> asciiEncodedLetters = new ArrayList<List<Integer>>();
		List<Integer> innerList = new ArrayList<Integer>();
		innerList.add(Integer.valueOf(108));
		innerList.add(Integer.valueOf(111));
		innerList.add(Integer.valueOf(111));
		innerList.add(Integer.valueOf(107));
		asciiEncodedLetters.add(innerList);
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		List<Integer> innerList2 = new ArrayList<Integer>();
		innerList2.add(Integer.valueOf(11));
		innerList2.add(Integer.valueOf(14));
		innerList2.add(Integer.valueOf(14));
		innerList2.add(Integer.valueOf(10));
		expected.add(innerList2);
		List<List<Integer>> actual = Utils.removeASCIIEncoding(asciiEncodedLetters);
		assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testConvertWordsToASCIIDecimal()
	{
		List<String> words = new ArrayList<String>();
		words.add("look");
		words.add("a");
		words.add("bird");
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		List<Integer> listForLook = new ArrayList<Integer>();
		listForLook.add(Integer.valueOf(108));
		listForLook.add(Integer.valueOf(111));
		listForLook.add(Integer.valueOf(111));
		listForLook.add(Integer.valueOf(107));
		List<Integer> listForA = new ArrayList<Integer>();
		listForA.add(Integer.valueOf(97));
		List<Integer> listForBird = new ArrayList<Integer>();
		listForBird.add(Integer.valueOf(98));
		listForBird.add(Integer.valueOf(105));
		listForBird.add(Integer.valueOf(114));
		listForBird.add(Integer.valueOf(100));
		expected.add(listForLook);
		expected.add(listForA);
		expected.add(listForBird);
		List<List<Integer>> actual = Utils.convertWordsToASCIIDecimal(words);
		assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testEncrypt()
	{
		List<String> words = new ArrayList<String>();
		words.add("look");
		String expected = "AZFB";
		String actual = Utils.encrypt(words, Integer.valueOf(2), Integer.valueOf(15));
		assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testDecrypt()
	{
		String ciphertext = "AZFB";
		String expected = "look";
		String actual = Utils.decrypt(ciphertext, Integer.valueOf(2), Integer.valueOf(15));
		assertTrue(expected.equals(actual));
	}
}
