package dk.heick.io.monitoring.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Assorted string utility methods.
 * @author Frederik Heick
 * @since 1.0
 */
public class StringUtils {
	
	
	/**
	 * Converts a <code>Iterator&lt;String&gt;</code> into a sorted <code>List</code>. 
	 * @param it the iterator
	 * @return a sorted list of strings
	 */
	public final static List<String> asList(Iterator<String> it) {		
		return asStringList(it);
	}
	
	/**
	 * Converts a Iterator of Strings to a StringList.
	 * @param it the Iterator of Strings
	 * @return a StringList with the strings in the iterator, sorted.
	 * @since 2.0
	 */
	public final static StringList asStringList(Iterator<String> it) {
		StringList list = new StringList();
		while (it.hasNext()) {
			list.add(it.next());
		}
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Will determine if a String contains an empty value.
	 * @param s the string
	 * @return <code>true</code> if the string is either <code>null</code> or has a length of zero after a trim, otherwise <code>false</code>.
	 */
	public final static boolean isEmpty(String s) {
		if (s==null) {
			return true;
		} else if (s.length()==0) {
			return true;
		} else if (s.trim().length()==0) {
			return true;
		} else {
			return false;
		}				
	}

	/**
	 * Returns a string, which is all the Strings in the list, with "\n"  between the strings.
	 * @param list the list of strings
	 * @return a string of all the strings with "\n" in between.
	 */
	public final static String asString(List<String> list) {
		return asString(list,"\n");
	}
	
	/**
	 * Returns a list of Strings and appends them to one list, with the divider in between.
	 * @param list the list of strings
	 * @param divider the divider.
	 * @return a string with with all the strings, with the divider in between.
	 */
	public final static String asString(List<String> list,String divider) {
		StringBuilder result = new StringBuilder();
		boolean first=true;
		for (String s : list) {
			if (!first) {
				result.append(divider);				
			} else {				
				first=false;
			}
			result.append(s);			
		}
		return result.toString().trim();
	}

	/**
	 * Returns a converted string where all "<code>&#36;&#123;name&#125;</code>" is replace with the corresponding system properties value, if the <tt>name</tt> is a valid system properties name. <br> 
	 * <b>Important</b>:This is NOT case sensitive.
	 * @param value the value to be converted
	 * @return a string where all "<code>&#36;&#123;name&#125;</code>" is replaced with System.properties value, if that value is not <code>null</code>.
	 * @see java.lang.System#getProperty(String)
	 * @see java.lang.System#getProperties()
	 */
	public final static String replaceSystemProperties(String value) {
		if (value==null) {
			return null;
		} else {			
			int posA = value.indexOf("${");
			int posB = value.indexOf("}");
			if ( (posA!=-1) && (posB!=-1) && (posB>posA)) {
				String name = value.substring(posA+2,posB).trim().toLowerCase();
				String replacingValue = getCaseInsensitiveSystemProperty(name);
				if (replacingValue!=null) {
					value = value.substring(0,posA)+replacingValue+value.substring(posB+1);
					return replaceSystemProperties(value);
				} else {
					return value;
				}
			} else {
				return value;
			}
		}
	}
	
	/**
	 * Returns a converted string where all "<code>&#36;&#123;name&#125;</code>" is replace with the corresponding env properties value, if the <tt>name</tt> is a valid env properties name. <br>	 
	 * <b>Important</b>:This is NOT case sensitive.
	 * @param value the value to be converted
	 * @return a string where all "<code>&#36;&#123;name&#125;</code>" is replaced with System.getenv() value, if that value is not <code>null</code>.
	 * @see java.lang.System#getenv()
	 * @see java.lang.System#getenv(String)
	 */
	public final static String replaceEnvironmentProperties(String value) {
		if (value==null) {
			return null;	
		} else {
			int posA = value.indexOf("${");
			int posB = value.indexOf("}");
			if ( (posA!=-1) && (posB!=-1) && (posB>posA)) {
				String name = value.substring(posA+2,posB).trim();
				String replacingValue = getCaseInsensitiveEnvironmentProperty(name);
				if (replacingValue!=null) {
					value = value.substring(0,posA)+replacingValue+value.substring(posB+1);
					return replaceEnvironmentProperties(value);
				} else {
					return value;
					}
			} else {
				return value;
			}
		}
	}
	
	/**
	 * Finds a system property with the key value.
	 * @param key the key to look for in <code>System.getProperty(String)</code>.
	 * @return return the value in the <code>System.getProperty(String)</code> which has the "key", case insensive, otherwise returns <code>null</code>.  
	 * @see System#getProperty(String)
	 */
	protected final static String getCaseInsensitiveSystemProperty(String key) {
		return getCaseInsensitiveProperty(key, System.getProperties());
	}	
	
	/**
	 * Finds a system env with the key value.
	 * @param key the key to look for in <code>System.getenv(String)</code>.
	 * @return return the value in the <code>System.getenv(String)</code> which has the "key", case insensive, otherwise returns <code>null</code>.  
	 * @see System#getenv(String)
	 */
	protected final static String getCaseInsensitiveEnvironmentProperty(String key) {			
		String result = null;
		Iterator<String> keys = System.getenv().keySet().iterator();
		while ((result==null) && (keys.hasNext())) {
			String envKey = keys.next();
			if (envKey.equalsIgnoreCase(key)) {
				result = System.getenv(envKey);
			}
		}
		return result;
	}
	
	protected final static String getCaseInsensitiveProperty(String key,Properties properties) {
		String result = null;
		Iterator<String> keys = properties.stringPropertyNames().iterator();
		while ((result==null) && (keys.hasNext())) {
			String envKey = keys.next();
			if (envKey.equalsIgnoreCase(key)) {
				result = properties.getProperty(envKey);
			}
		}
		return result;
	}

	/**
	 * Determines if a String value is contained in a String array.
	 * @param array the String array.
	 * @param value the String value.
	 * @param ignoreCase when comparing strnig, shall case be ignored.
	 * @return <code>false</code> if <tt>array</tt> is <code>null</code> or empty, or <tt>value</tt> is <code> null</code> or not in array, otherwise <code>true</code>.
	 */
	public final static boolean doArrayContains(String[] array,String value,boolean ignoreCase) {
		if (array==null) {
			return false;
		} else if (array.length==0) {
			return false;
		} else if (value==null) {
			return false;			
		} else {
			for ( final String elememt : array ) {
				if (ignoreCase) {
					if (value.equalsIgnoreCase(elememt)) {
						return true;
					}
				} else {
					if (value.equals(elememt)) {
						return true;
					}
				}		        
			}
		    return false;
		}
	}
		
	/**
	 * Left pads the a string with space until has a certain length.
	 * @param value the string value to pad
	 * @param length the desired length of the return string value 
	 * @return if <code>null</code>, left pad values until length, if the string value length is greater than the desired length the value is returned, otherwise the pad value if padded on the left until the desired length is reached.
	 */	
	public final static String leftPad(String value,int length) {
		return leftPad(value,' ',length);
	}
	
	/**
	 * Left pads the a string with a char until has a certain length.
	 * @param value the string value to pad
	 * @param pad the char value to left pad with, if <code>null</code> or empty a white space is used.
	 * @param length the desired length of the return string value 
	 * @return if <code>null</code>, left pad values until length, if the string value length is greater than the desired length the value is returned, otherwise the pad value if padded on the left until the desired length is reached.
	 */
	public final static String leftPad(String value,char pad,int length) {
		if (value==null) {
			value="";
		} 
		if (value.length()>=length) {
			return value;
		} else {
			StringBuilder sb = new StringBuilder(value);
			while (sb.length()<length) {
				sb.insert(0, pad);
			}
			return sb.substring(0,length);
		}
	}
	
	/**
	 * Right pads the a string with a space until has a certain length.
	 * @param value the string value to pad
	 * @param length the desired length of the return string value 
	 * @return if <code>null</code>, left pad values until length, if the string value length is greater than the desired length the value is returned, otherwise the pad value if padded on the right until the desired length is reached.
	 */	
	public final static String rightPad(String value,int length) {
		return rightPad(value,' ',length);
	}
	
	/**
	 * Right pads the a string with a char until has a certain length.
	 * @param value the string value to pad
	 * @param pad the char value to right pad with, if <code>null</code> or empty a white space is used.
	 * @param length the desired length of the return string value 
	 * @return if <code>null</code>, left pad values until length, if the string value length is greater than the desired length the value is returned, otherwise the pad value if padded on the right until the desired length is reached.
	 */	
	public final static String rightPad(String value,char pad,int length) {
		if (value==null) {
			value="";
		} 
		if (value.length()>=length) {
			return value;
		} else {
			StringBuilder sb = new StringBuilder(value);
			while (sb.length()<length) {
				sb.append(pad);
			}
			return sb.substring(0,length);
		}	
	}
	
	
	/**
	 * Split a string in to bites divide in certain <tt>maxLineLength</tt> by "<tt>lineDivider</tt>". If the string is longer than <tt>maxLength</tt> than it is cut to this length and
	 * <tt>maxLengthExceededIndicator</tt> is added if it is not <code>null</code>.
	 * @param s the string
	 * @param maxLength the total max length of the string.
	 * @param maxLineLength the max length of each section which should be seperate by a "lineDivider".
	 * @param lineDivider the line divider betweeen two sections of the string of "maxLineLength", if <code>null</code> than not used. 
	 * @param maxLengthExceededIndicator append on the end of string to indicate that more was/is present, could be "..." or "...(more)", if <code>null</code> than ignored.
	 * @return if <tt>s</tt> is <code>null</code> then <code>null</code> is returned, otherwise see method description.
	 * @see #bite(String, int, String)
	 */
	public final static String bite(String s,int maxLength,int maxLineLength,String lineDivider,String maxLengthExceededIndicator) {
		if (s==null) {
			return s;
		} else {
			if ((maxLength>0) && (s.length()>maxLength)) {
				s = s.substring(0,maxLength);
				if (maxLengthExceededIndicator!=null) {
					s = s + maxLengthExceededIndicator;
				}				
			}
			return bite(s, maxLineLength, lineDivider);
		}
	}
	
	
	/**
	 * Split a string in to bites divide in certain lengths by "lineDivider". 
	 * @param s the string to divide.
	 * @param maxLineLength the max length of each line.
	 * @param lineDivider the line divider, if <code>null</code>, than not used.
	 * @return a string divide by "lineDivider" until it is below length.
	 */
	public final static String bite(String s,int maxLineLength,String lineDivider) {
		if (s==null) {
			return s;
		} else if (s.length()<=maxLineLength) {
			return s;
		} else {
			StringBuilder sb = new StringBuilder();
			while (s.length()>maxLineLength) {
				sb.append(s.substring(0,maxLineLength));
				if (lineDivider!=null) {
					sb.append(lineDivider) ;
				}
				s = s.substring(maxLineLength);
			}
			sb.append(s);
			return sb.toString();			
		}
	}
	
	/**
	 * The following method converts all the letters into upper/lower case, depending on their position near a space or other special chars.
	 * @param s the string to capatilize.
	 * @return all characters is lowercased, except if it is the first char, or the next characters is whitespace, dot or backslash.
	 * @since 2.0
	 * @see Character#isWhitespace(char)
	 */
	public final static String capitalize(String s) {
		char[] chars = s.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	/**
	 * Converts a exception stacktrace to String. 
	 * @param e the exception
	 * @return if <tt>e</tt> is <code>null</code>, "[null]" is returned, otherwise the complete stacktrace of the exception as a String. 
	 */
	public final static String getStacktrace(Exception e) {
		if (e==null) {
			return "[null]";
		} else {
			Writer result = null;
			PrintWriter printWriter = null;
			try {
				result = new StringWriter();
				printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);				
				return result.toString();
			} finally {
				if (printWriter!=null) {
					printWriter.close();
				}
				if (result!=null) {
					try {
						result.close();
					} catch (IOException e1) {						
					}
				}
			}		
		}
	}
	
	/**
	 * TODO
	 * @param b
	 * @return
	 */
	public static final String getYesNo(boolean b) {
		if (b) {
			return "yes";
		} else {
			return "no";
		}
	}

}
