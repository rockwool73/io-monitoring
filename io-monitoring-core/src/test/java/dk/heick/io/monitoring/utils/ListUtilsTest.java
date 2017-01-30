package dk.heick.io.monitoring.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class ListUtilsTest {

	@Test
	public void testAsListEnumerationOfT() {
		List<String> mylist = new ArrayList<String>();
		mylist.add("a");
		mylist.add("c");
		mylist.add("b");
		//
		Enumeration<String> e= Collections.enumeration(mylist);
		//
		List<String> result = ListUtils.asList(e);
		assertEquals(3, result.size());
		assertEquals(mylist, result);
	}

	@Test
	public void testAsListIteratorOfT() {
		List<String> mylist = new ArrayList<String>();
		mylist.add("a");
		mylist.add("c");
		mylist.add("b");
		//
		Iterator<String> it = mylist.iterator();
		//
		List<String> result = ListUtils.asList(it);
		assertEquals(3, result.size());
		assertEquals(mylist, result);
	}

	@Test
	public void testAsListTArray() {
		List<String> mylist = new ArrayList<String>();
		mylist.add("a");
		mylist.add("c");
		mylist.add("b");
		//
		String[] arr = {"a","c","b"};
		List<String> result = ListUtils.asList(arr);
		assertEquals(3, result.size());
		assertEquals(mylist, result);
	}

	@Test
	public void testAddToList() {
		List<String> mylist = new ArrayList<String>();
		mylist.add("a");
		mylist.add("c");
		mylist.add("b");
		//
		String[] arr = {"a","c","b"};
		List<String> result = ListUtils.addToList(null, arr);
		assertEquals(3, result.size());
		assertEquals(mylist, result);
		//
		String[] arr2 = {"d","e"};
		ListUtils.addToList(result, arr2);
		assertEquals(5, result.size());
	}

}
