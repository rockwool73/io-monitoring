package dk.heick.io.monitoring.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Static methods to convert X to sorted instances of <code>List&lt;T&gt;</code>.
 * @author Frederik Heick
 * @since 1.0
 */
public class ListUtils {

	/**
	 * No constructor, only static synchronized methods.
	 */
	private ListUtils() {
		super();
	}
	
	/**
	 * Converts a the elements in an enumaration to a unsorted list. 
	 * @param <T> The generic object.
	 * @param en an enumateration of objects that implements Comparable
	 * @return a list of the enumaration elements, if the enumaration is <code>null</code> an empty list is returned.
	 * @throws NullPointerException if any element in the enumaration is <code>null</code>
	 */
	public static <T> List<T> asList(Enumeration<T> en) throws NullPointerException {
		List<T> result = new ArrayList<T>();
		if (en!=null) {
			while (en.hasMoreElements()) {
				result.add(en.nextElement());
			} 				
		}
		return result;
	}
	

	/**
	 * Converts a the elements in an iterator to a unsorted list.
	 * @param <T> The generic object.
	 * @param it an iterator of objects that implements Comparable
	 * @return a list of the iterator elements, if the iterator is <code>null</code> an empty list is returned.
	 * @throws NullPointerException if any element in the iterator is <code>null</code>
	 */
	public static <T> List<T> asList(Iterator<T> it) throws NullPointerException {
		List<T> result = new ArrayList<T>();
		if (it!=null) {
			while (it.hasNext()) {
				result.add(it.next());
			} 				
		}
		return result;
	}

	
	/**
	 * Converts a the elements in an array to a unsorted list.
	 * @param <T> The generic object.
	 * @param arr array of objects that implements Comparable
	 * @return a list of the array elements, if the array is <code>null</code> an empty list is returned.
	 * @throws NullPointerException if the <tt>arr</tt> is <code>null</code>.
	 */
	public static <T> List<T> asList(T[] arr) throws NullPointerException {
		List<T> result = new ArrayList<T>();
		if (arr!=null) {
			for (T obj : arr) {
				result.add(obj);
			}
		}
		return result;
	}

	
	
	/**
	 * Add the all the array elements to a list.
	 * @param <T> The generic object.
	 * @param list the list where to add the array elements, if list is <code>null</code> an new instance is created.
	 * @param anArray the array with elements 
	 * @throws NullPointerException if any element in the array is <code>null</code>.
	 */
	public static <T> List<T> addToList(List<T> list,T[] anArray) throws NullPointerException {
		if (list==null) {
			list = new ArrayList<T>();
		}
		if ((anArray!=null) && (anArray.length>0)) {
			 for(T t : anArray) {
				 list.add(t);
			 }
		}	
		return list;
	}
	


}
