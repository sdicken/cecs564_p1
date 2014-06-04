package cecs564_p1;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class UtilsTest 
{
	private Integer A_KEY;
	private Integer K_KEY;
	private Integer X0;
	
	@Before
	public void setUp()
	{
		A_KEY = 2;
		K_KEY = 15;
		X0 = 0;
	}
	
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
		List<Integer> expected = new ArrayList<Integer>();
		expected.add(Integer.valueOf(11));
		expected.add(Integer.valueOf(14));
		expected.add(Integer.valueOf(14));
		expected.add(Integer.valueOf(10));
		List<Integer> actual = Utils.removeASCIIEncoding(asciiEncodedLetters);
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
		String actual = Utils.encrypt(words, A_KEY, K_KEY, X0);
		assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testDecrypt()
	{
		String ciphertext = "AZFB";
		String expected = "look";
		String actual = Utils.decrypt(ciphertext, A_KEY, K_KEY, X0, false);
		assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testAttackBruteForce()
	{
		String ciphertext = "IIHJNCDMJOQIHOBFXAZL";
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put(Utils.A_KEY, 2);
		expected.put(Utils.K_KEY, 15);
		expected.put(Utils.X0, 0);
		Map<String, Integer> actual = Utils.attack(ciphertext, false);
		assertTrue(expected.equals(actual));
	}
	
	@Test
	public void testEnglishAnalysis()
	{
		String combinedWords = "themanoverthereisbig";
		Integer expected = 7;
		Integer actual = Utils.performEnglishAnalysis(combinedWords);
		assertTrue(expected.equals(actual));
	}
}
