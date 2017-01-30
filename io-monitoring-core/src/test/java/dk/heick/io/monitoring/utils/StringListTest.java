package dk.heick.io.monitoring.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

public class StringListTest {

	@Test
	public void testStringList() {
		StringList sl = new StringList();
		assertNotNull(sl);
		assertEquals(0, sl.size());
	}

	@Test
	public void testStringListListOfString() {
		List<String> mylist = new ArrayList<String>();
		mylist.add("a");
		mylist.add("c");
		mylist.add("b");
		//
		StringList sl = new StringList(mylist);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		assertEquals("a",sl.get(0));
		assertEquals("c",sl.get(1));
		assertEquals("b",sl.get(2));
	}

	@Test
	public void testStringListString() {
		StringList sl = new StringList("a");
		assertNotNull(sl);
		assertEquals(1, sl.size());
		assertEquals("a",sl.get(0));
	}

	@Test
	public void testStringListStringArray() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		assertEquals("a",sl.get(0));
		assertEquals("c",sl.get(1));
		assertEquals("b",sl.get(2));
	}

	@Test
	public void testStringListObjectArray() {
		Object[] arr = {"a",null,"b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(2, sl.size());
		assertEquals("a",sl.get(0));		
		assertEquals("b",sl.get(1));
	}

	@Test
	public void testAsUnmodifiableList() {
		StringList sl = new StringList("a");
		assertNotNull(sl);
		assertEquals(1, sl.size());
		assertEquals("a",sl.get(0));
		List<String> unlist = sl.asUnmodifiableList();
		assertNotNull(unlist);
		assertEquals(1, sl.size());
		assertEquals("a",sl.get(0));
	}

	@Test
	public void testAsList() {
		StringList sl = new StringList("a");
		assertNotNull(sl);
		assertEquals(1, sl.size());
		assertEquals("a",sl.get(0));
		List<String> unlist = sl.asList();
		assertNotNull(unlist);
		assertEquals(1, sl.size());
		assertEquals("a",sl.get(0));
	}

	@Test
	public void testSort() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		assertEquals("a",sl.get(0));
		assertEquals("c",sl.get(1));
		assertEquals("b",sl.get(2));
		//
		sl.sort();
		assertEquals(3, sl.size());
		assertEquals("a",sl.get(0));
		assertEquals("b",sl.get(1));
		assertEquals("c",sl.get(2));
	}

	@Test
	public void testSubListIntInt() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		//
		StringList result;
		//
		result = sl.subList(0,0);
		assertNotNull(result);
		assertEquals(0, result.size());
		//
		result = sl.subList(0,1);
		assertNotNull(result);
		assertEquals(1, result.size());
		//
		result = sl.subList(0,2);
		assertNotNull(result);
		assertEquals(2, result.size());
		//
		result = sl.subList(0,3);
		assertNotNull(result);
		assertEquals(3, result.size());
		//
		result = sl.subList(5,8);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testAsStringArray() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		assertEquals("a",sl.get(0));
		assertEquals("c",sl.get(1));
		assertEquals("b",sl.get(2));
		//
		String[] arr2 =sl.asStringArray();
		Objects.deepEquals(arr, arr2);
	}

	@Test
	public void testToString() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		assertEquals("acb",sl.toString());
	}

	@Test
	public void testToStringString() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		assertEquals("a,c,b",sl.toString(","));
	}

	@Test
	public void testIndexOfIgnoreCase() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		//
		assertEquals(0, sl.indexOfIgnoreCase("a"));		
		assertEquals(0, sl.indexOfIgnoreCase("A"));
		//
		assertEquals(1, sl.indexOfIgnoreCase("c"));		
		assertEquals(1, sl.indexOfIgnoreCase("C"));
		//
		assertEquals(2, sl.indexOfIgnoreCase("b"));		
		assertEquals(2, sl.indexOfIgnoreCase("B"));
		//
		assertEquals(-1, sl.indexOfIgnoreCase("d"));		
		assertEquals(-1, sl.indexOfIgnoreCase("D"));
	}

	@Test
	public void testIsValidIndex() {
		String[] arr = {"a","c","b"};
		StringList sl = new StringList(arr);
		assertNotNull(sl);
		assertEquals(3, sl.size());
		//
		assertTrue(sl.isValidIndex(0));
		assertTrue(sl.isValidIndex(1));
		assertTrue(sl.isValidIndex(2));
		//
		assertFalse(sl.isValidIndex(-1));
		assertFalse(sl.isValidIndex(4));
	}

}
