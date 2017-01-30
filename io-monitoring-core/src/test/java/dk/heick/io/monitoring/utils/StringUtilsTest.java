package dk.heick.io.monitoring.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testAsList() {
		List<String> list = new ArrayList<String>();
		list.add("horse");
		list.add("cat");
		list.add("dog");
		//
		List<String> result = StringUtils.asList(list.iterator());
		assertEquals(3, result.size());
		assertEquals("cat", result.get(0));
		assertEquals("dog", result.get(1));
		assertEquals("horse", result.get(2));
	}
	
	@Test
	public void testIsEmpty() {
		assertTrue(StringUtils.isEmpty(null));
		assertTrue(StringUtils.isEmpty(""));
		assertTrue(StringUtils.isEmpty(" "));
		assertTrue(StringUtils.isEmpty("   "));
		assertFalse(StringUtils.isEmpty("a"));
		assertFalse(StringUtils.isEmpty("a "));
		assertFalse(StringUtils.isEmpty(" a"));
		assertFalse(StringUtils.isEmpty(" a "));
	}

	@Test
	public void testAsStringListOfString() {
		List<String> list = new ArrayList<String>();
		list.add("horse");
		list.add("cat");
		list.add("dog");
		//
		String result = StringUtils.asString(list);
		assertEquals("horse\ncat\ndog", result);
	}

	@Test
	public void testAsStringListOfStringString() {
		List<String> list = new ArrayList<String>();
		list.add("horse");
		list.add("cat");
		list.add("dog");
		//
		String result1 = StringUtils.asString(list,"\n");
		assertEquals("horse\ncat\ndog", result1);
		//
		String result2 = StringUtils.asString(list,";");
		assertEquals("horse;cat;dog", result2);
		//
		String result3 = StringUtils.asString(list,"AB");
		assertEquals("horseABcatABdog", result3);		
	}
	
	@Test 
	public void testDoArrayContains() {
		String[] a1 = null;
		String[] a2 = {};
		String[] a3 = {"hest",null,"Ko","", "  ","GrIS"};
				
		//public static boolean doArrayContains(String[] array,String value,boolean ignoreCase) {
		assertFalse(StringUtils.doArrayContains(a1, "foo", true));
		assertFalse(StringUtils.doArrayContains(a1, "foo", false));
		assertFalse(StringUtils.doArrayContains(a2, null, true));
		assertFalse(StringUtils.doArrayContains(a2, null, false));
		assertFalse(StringUtils.doArrayContains(a3, null, true));
		assertFalse(StringUtils.doArrayContains(a3, null, false));
		//
		assertTrue(StringUtils.doArrayContains(a3, "ko", true));
		assertTrue(StringUtils.doArrayContains(a3, "Ko", true));
		assertTrue(StringUtils.doArrayContains(a3, "KO", true));
		assertTrue(StringUtils.doArrayContains(a3, "HEST", true));
		assertTrue(StringUtils.doArrayContains(a3, "GRIS", true));
		//
		assertTrue(StringUtils.doArrayContains(a3, "GrIS", false));
		assertTrue(StringUtils.doArrayContains(a3, "Ko", false));
		assertTrue(StringUtils.doArrayContains(a3, "hest", false));
		//
		assertFalse(StringUtils.doArrayContains(a3, "  hest ", false));
		//		
		assertFalse(StringUtils.doArrayContains(a3, "gris", false));
		assertFalse(StringUtils.doArrayContains(a3, "ko", false));
		assertFalse(StringUtils.doArrayContains(a3, "hESt", false));
	}

	@Test
	public void testreplaceSystemProperties() {
		String s_a1="jeg_hedder_frede";
		String s_a2="jeg_hedder_frede";
		String s_b1="${java.vendor}_jeg_${java.vendor}_hedder_${java.vendor}_frede_${java.vendor}";
		String s_b2="Oracle Corporation_jeg_Oracle Corporation_hedder_Oracle Corporation_frede_Oracle Corporation";
		//		
		assertEquals(s_a2,StringUtils.replaceSystemProperties(s_a1));
		assertEquals(s_b2,StringUtils.replaceSystemProperties(s_b1));
		assertEquals("",StringUtils.replaceSystemProperties(""));
		assertEquals("  ",StringUtils.replaceSystemProperties("  "));
		assertNull(StringUtils.replaceSystemProperties(null));			
	}
	
	@Test
	public void testReplaceEnvironmentProperties() {
		//This testcase might not work on all systems, tested on windows
		String s_a1="jeg_hedder_frede";
		String s_a2="jeg_hedder_frede";
		
		String s_b1="${USERNAME}_jeg_${Username}_hedder_${UserName}_frede_${username}";
		String s_un=StringUtils.getCaseInsensitiveEnvironmentProperty("USERNAME");
		String s_b2=s_un+"_jeg_"+s_un+"_hedder_"+s_un+"_frede_"+s_un;

		assertEquals(s_a2,StringUtils.replaceEnvironmentProperties(s_a1));
		assertEquals(s_b2,StringUtils.replaceEnvironmentProperties(s_b1));
		assertEquals("",StringUtils.replaceEnvironmentProperties(""));
		assertEquals("  ",StringUtils.replaceEnvironmentProperties("  "));
		assertNull(StringUtils.replaceEnvironmentProperties(null));
	}

	
	@Test
	public void testLeftPad() {
		assertEquals("HHHHab", StringUtils.leftPad("ab", 'H', 6));
		assertEquals("1111ab", StringUtils.leftPad("ab", '1', 6));
	}
	
	@Test
	public void testRightPad() {		
		assertEquals("abHHHH", StringUtils.rightPad("ab", 'H', 6));
	}
	
	@Test
	public void testBite() {
		assertEquals("aaa<br/>bbb<br/>ccc",StringUtils.bite("aaabbbccc", 3,"<br/>"));		
		assertEquals("aaa<br/>bbb<br/>ccc<br/>dd",StringUtils.bite("aaabbbcccdd", 3,"<br/>"));	
	}
	
	@Test
	public void testCapitalize() {
		// white,dot,slash
		assertEquals("Jeg Hedder Frede ",StringUtils.capitalize("JeG hedder frede "));
		assertEquals("Jeg.Hedder.Frede ",StringUtils.capitalize("JeG.hedder.frede "));
		assertEquals("Jeg\'Hedder\'Frede ",StringUtils.capitalize("JeG\'hedder\'frede "));
		assertEquals("   ",StringUtils.capitalize("   "));
		assertEquals(" A ",StringUtils.capitalize(" a "));
	}
}
