package dk.heick.io.monitoring.processor;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.ValidationException;



/**
 * An abstract that converts a file to XML/JAXB instance. <br> 
 * You must implement the method <tt>process(T)</tt>.<br>
 * @author Frederik Heick
 * @version 1.0
 * @param <T> class that must be annotated with &#64;XmlRootElement 
 */
@XmlRootElement
public abstract class XMLFileProcessor<T> extends DefaultFileProcessor {
	
	private Class<?> annotatedClass=null;	
	private String encoding;
	private JAXBContext jaxbContext=null;
	private Unmarshaller unmarshaller=null;
	
	/**
	 * Constructor
	 * @param annotatedClass the annotated XML class which is constructed using the JAXBContextFactory
	 * @param encoding the encoding schema to load the file. If <code>null</code> the default unmarshaller is used.
	 * @see JAXBContext
	 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html">https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html</a>
	 */
	public XMLFileProcessor(Class<T> annotatedClass,String encoding) {
		super();
		this.annotatedClass=annotatedClass;		
	}

	/**
	 * Constructor, using default encoding schema.
	 * @param annotatedClass the annotated XML class which is constructed using the JAXBContextFactory
	 */
	public XMLFileProcessor(Class<T> annotatedClass) {
		this(annotatedClass,null);
	}
	
	
	@Override
	public void validate() throws ValidationException {
		ValidateUtils.validateNotNull("AnnotatedClass", getAnnotatedClass());		
		try {
			ValidateUtils.validateNotNull("JAXBContext", getContext());			
		} catch (JAXBException e) {
			ValidateUtils.validateFailed("JAXBContext",e);
		}
		try {			
			ValidateUtils.validateNotNull("Unmarshaller",getUnmarshaller());
		} catch (JAXBException e) {
			ValidateUtils.validateFailed("Unmarshaller",e);
		}		
	}
	
	/**
	 * Gets the JAXB context, intialized once.
	 * @return a JAXBContext the JAXBContext for the annotated class.
	 * @throws JAXBException if not able to create a JAXB Context.
	 * @see #getAnnotatedClass()
	 */
	public final JAXBContext getContext() throws JAXBException {
		if (jaxbContext==null) {
			jaxbContext = JAXBContext.newInstance(getAnnotatedClass());
		}
		return jaxbContext;
	}
	
	public final Class<?> getAnnotatedClass() {
		return annotatedClass;
	}
	
	/**
	 * Lazy initializing a Unmarshaller which is stored in the class for future use.
	 * @return an Unmarshaller instance. 
	 * @throws JAXBException unable to create the conetxt or the unmarshaller.
	 */
	public final Unmarshaller getUnmarshaller() throws JAXBException {
		if (unmarshaller==null) {
			unmarshaller = getContext().createUnmarshaller();
		}
		return unmarshaller;
	}
	
	/**
	 * The file encoding if any.
	 * @return the encoding to use when loading the file, if <code>null</code>, default encoding is used.
	 */
	public String getEncoding() {
		return encoding;
	}

	@Override
	public void process(Properties context,File file) throws Exception {		
		if (getEncoding()==null) {
			@SuppressWarnings("unchecked")
			T xml = ((T)(getUnmarshaller().unmarshal(file)));
			process(context,xml);				
		} else {
			InputStream inputStream = null;
			Reader reader = null;
			try {
				inputStream = new FileInputStream(file);
				reader = new InputStreamReader(inputStream, getEncoding());
				@SuppressWarnings("unchecked")
				T xml = ((T) unmarshaller.unmarshal(reader));
				process(context,xml);
			} finally  {
				close(reader);
				close(inputStream);				
			}
		}
	}
	
	private void close(Closeable c) {
		try {
			if (c!=null) {
				c.close();
			}
		} catch (IOException e) {			
		}
	}

	/**
	 * What to do with the XML object.
	 * @param context the properties context initialized in the "beforeProcess".
	 * @param xml the xml instance.
	 * @throws Exception any exception in processing the XML.
	 * @see #beforeProcess(Properties, File)
	 */
	public abstract void process(Properties context,T xml) throws Exception;
	

	
	
	

}
