package dk.heick.io.monitoring.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Convenience class for a list of strings, extension of <code>ArrayList&lt;String&gt;</code>. <br>
 * Gives the ability to add format "adds" if nessecary.
 * @author Frederik Heick
 * @since 1.9
 */
public class StringList extends ArrayList<String> {

	private static final long serialVersionUID = 8185728090343873214L;

	/**
	 * Default constructor
	 */
	public StringList() {
		super();
	}
	
	/**
	 * Constructs a StringList on the basis of another StringList.
	 * @param list the other list to construct with, if the <tt>list</tt> is <code>null</code>, it is ignored.
	 * @since 1.9
	 */
	public StringList(List<String> list) {
		super();
		if (list!=null) {
			addAll(list);
		}
	}
	
	/**
	 * Constructs a StringList which starts with one string.
	 * @param s a string which is added as the first entry. If the <tt>s</tt> is <code>null</code>, it is ignored.
	 * @since 2.0
	 */
	public StringList(String s) {
		super();
		if (s!=null) {
			add(s);
		}
	}
	
	/**
	 * Constructs a StringList on the basis of an array of strings.
	 * @param strings an array of strings which is added. If the <tt>strings</tt> is <code>null</code>, it is ignored.
	 * @since 2.0
	 */
	public StringList(String[] strings) {
		super();
		if (strings!=null) {
			for (String s : strings) {
				add(s);
			}
		}
	}
	
	/**
	 * Constructs a StringList on the basis of an array of Objects.
	 * @param objs an array of Objects which is added as "toString()". If the <tt>objs</tt> is <code>null</code>, it is ignored, or any of the objects in the array is <code>null</code> it is ignored.
	 * @since 2.0
	 */
	public StringList(Object[] objs) {
		super();
		if (objs!=null) {
			for (Object obj : objs) {
				if (obj!=null) {
					add(obj.toString());
				}
			}
		}
	}


	
	/**
	 * Returns this list as an  unmodifiableList of strings.
	 * @return an unmodifiableList of strings.
	 * @see Collections#unmodifiableList(List)
	 */
	public final List<String> asUnmodifiableList() {
		return Collections.unmodifiableList(this);
	}
	
	/**
	 * Returns the list as a list of Strings, and not a StringList instance.
	 * @return as a list of string
	 */
	public List<String> asList() {		
		return this;
	}
	
	/**
	 * Sorts the this list of strings using "Collections.sort()".
	 * @see Collections#sort(List)
	 */
	public void sort() {
		Collections.sort(this);
	}
	
	
	/**
	 * Gets the index of the first String in the StringList containing the certain text value.
	 * @param contains the text to search for.
	 * @param caseSensitive if the search shall be case sensitive or not.
	 * @return if a line is found the index is returned, otherwise -1 is returned.
	 */
	public final int getIndexForFirstContainLine(String contains,boolean caseSensitive) {
		if (size()==0) {
			return -1;
		} else {
			for (int index=0;index<size();index++) {
				String line = get(index);
				if (caseSensitive) {
					if (line.contains(contains)) {
						return index;
					}
				} else {
					if (line.toLowerCase().contains(contains.toLowerCase())) {
						return index;
					}
				}
			}
			return -1;
		}			
	}
	
	
	/**
	 * Gets the line of the first String in the StringList containing the certain text value.
	 * @param contains the text to search for.
	 * @param caseSensitive if the search shall be case sensitive or not.
	 * @return if a line is found the line is returned, otherwise <code>null</code> is returned.
	 */
	public final String getLineForFirstContainLine(String contains,boolean caseSensitive) {
		int index = getIndexForFirstContainLine(contains, caseSensitive);
		if (index<0) {
			return null;
		} else {
			return get(index);
		}		
	}
	
	
	/**
	 * Get all the lines that contains a certain text value in a seperate StringList
	 * @param contains the text to search for.
	 * @param caseSensitive if the search shall be case sensitive or not.
	 * @return a StringList with all the lines containing the text value.
	 */
	public final StringList getAllThatContains(String contains,boolean caseSensitive) {
		if (size()==0) {
			return new StringList();
		} else {
			StringList result = new StringList();
			for (int index=0;index<size();index++) {
				String line = get(index);
				if (caseSensitive) {
					if (line.contains(contains)) {
						result.add(line);
					}
				} else {
					if (line.toLowerCase().contains(contains.toLowerCase())) {
						result.add(line);;
					}
				}
			}						
			return result;
		}			
	}
	
	@Override
	public final StringList subList(int fromIndex, int toIndex) {	
		if ((isValidIndex(fromIndex)) && (isValidIndex(toIndex-1))) {
			return new StringList(super.subList(fromIndex, toIndex));
		} else {
			return new StringList();
		}
	}
	
	/**
	 * Returns the StringList as an array of Strings.
	 * @return a string array.
	 */
	public final String[] asStringArray() {
		String[] sa = new String[size()];
		return toArray(sa);
		
	}

	/**
	 * Appends all the strings in the list to one string.
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		//
		for (int index=0;index<size();index++) {
			s.append(get(index));
		}
		//
		return s.toString();
	}
	
	/**
	 * Appends all the strings in the list to one string, divided by the <tt>divider</tt>.
	 * @param divider the string divider to divide the string list entries.
	 * @return a String containing all the strings in the list with the <tt>divider</tt> between the entries.
	 */
	public String toString(String divider) {
		StringBuilder s = new StringBuilder();
		//
		for (int index=0;index<size();index++) {
			if (index!=0) {
				s.append(divider);
			}
			s.append(get(index));			
		}
		//
		return s.toString();
		
	}
	
	/**
	 * Makes an indexOf but with IgnoreCase on the string comparison.
	 * @param anotherString a string.
	 * @return -1 if <tt>anotherString</tt> is <code>null</code>, otherwise the index where string is contained in the list (case insensitive).
	 */
	public int indexOfIgnoreCase(String anotherString) {
		if (anotherString==null) {
			return -1;
		} else {
			int result = -1;
			int index=0;
			while ((result==-1) && (index<size())) {
				if (get(index).equalsIgnoreCase(anotherString)) {
					result = index;
				}
				index++;
			}
			return result;
		}
	}	
	
	/**
	 * Is the index valid for the String list.
	 * @param index the index in the String list.
	 * @return <code>true</code> if the <tt>index</tt> is inside the valid range of the StringList (0&lt;=index&lt;size()), otherwise <code>false</code>.
	 */
	public final boolean isValidIndex(int index) {
		return ((index>=0) && (index<size()));
	}

}
