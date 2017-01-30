package dk.heick.io.monitoring.validation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.junit.Test;

import dk.heick.io.monitoring.utils.TimeSpan;

public class ValidateUtilsTest {

	@Test
	public void testValidateNotNull() {
		try {
			ValidateUtils.validateNotNull("hest", "hest");			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateNotNull("hest", null);
			fail();
		} catch (ValidationException e) {		
		}
	}

	@Test
	public void testValidateNotEmptyStringString() {
		try {
			ValidateUtils.validateNotEmpty("name", "value");			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateNotEmpty("name", "");
			fail();
		} catch (ValidationException e) {
		}
		try {
			ValidateUtils.validateNotEmpty("name", "  ");
			fail();
		} catch (ValidationException e) {
		}
		try {
			ValidateUtils.validateNotEmpty("name", null);
			fail();
		} catch (ValidationException e) {
		}
	}

	@Test
	public void testValidateNotEmptyStringStringBoolean() {
		try {
			ValidateUtils.validateNotEmpty("name", "value",false);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateNotEmpty("name", "",false);
			fail();
		} catch (ValidationException e) {
		}
		try {
			ValidateUtils.validateNotEmpty("name", "  ",false);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateNotEmpty("name", null,false);
			fail();
		} catch (ValidationException e) {
		}
	}

	@Test
	public void testValidateLength() {		
		try {
			ValidateUtils.validateLength("name", null, false, 2, 6);
			fail();
		} catch (ValidationException e) {			
		}
		try {
			ValidateUtils.validateLength("name", "hans", false, null, null);
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateLength("name", "hans", false, 2, 6);
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateLength("name", "  hans  ", true, 2, 6);
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateLength("name", "  hans  ", false, 2, 6);
			fail();
		} catch (ValidationException e) {			
		}
		try {
			ValidateUtils.validateLength("name", "alexander", true, 2, null);
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateLength("name", "alexander", true, 2, 9);
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		try {
			ValidateUtils.validateLength("name", "hans", true, 6, 8);
			fail();
		} catch (ValidationException e) {			
		}
		try {
			ValidateUtils.validateLength("name", "hans", true, null, 8);			
		} catch (ValidationException e) {	
			fail(e.getMessage());
		}
	}

	@Test
	public void testValidateRangeStringIntegerIntegerInteger() {
		Integer value = null;
		Integer minValue = null;
		Integer maxValue = null;
		//
		try {
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			value=8;
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=6;
			value=8;
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=6;
			value=8;
			maxValue=10;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=9;
			value=8;
			maxValue=10;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			minValue=5;
			value=8;
			maxValue=7;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
	}

	@Test
	public void testValidateRangeStringLongLongLong() {
		Long value = null;
		Long minValue = null;
		Long maxValue = null;
		//
		try {
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			value=8L;
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=6L;
			value=8L;
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=6L;
			value=8L;
			maxValue=10L;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=9L;
			value=8L;
			maxValue=10L;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			minValue=5L;
			value=8L;
			maxValue=7L;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
	}

	@Test
	public void testValidateRangeStringDoubleDoubleDouble() {
		Double value = null;
		Double minValue = null;
		Double maxValue = null;
		//
		try {
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			value=8.0;
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=6.0;
			value=8.0;
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=6.0;
			value=8.0;
			maxValue=10.0;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=9.0;
			value=8.0;
			maxValue=10.0;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			minValue=5.0;
			value=8.0;
			maxValue=7.0;			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
	}

	@Test
	public void testValidateRangeStringDateDateDate() {
		Date value = null;
		Date minValue = null;
		Date maxValue = null;
		//
		try {
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			value=new Date(8L);
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=new Date(6L);
			value=new Date(8L);
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=new Date(6L);
			value=new Date(8L);
			maxValue=new Date(10L);			
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=new Date(9L);
			value=new Date(8L);
			maxValue=new Date(10L);			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			minValue=new Date(5L);
			value=new Date(8L);
			maxValue=new Date(7L);			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
	}

	@Test
	public void testValidateDirectoryStringPath() {
		Path directory = null;
		Path file = null;
		try {
			try {
				ValidateUtils.validateDirectory("name", directory);
				fail();
			} catch (ValidationException e) {			
			} 
			try {
				ValidateUtils.validateDirectory("name", new File("slædfksdflæksædlfksælfksdlæ").toPath());
				fail();
			} catch (ValidationException e) {			
			}
			//
			directory = Files.createTempDirectory("bente");
			file = Files.createTempFile("hans", ".txt");
			assertTrue(directory.toFile().exists());
			assertTrue(file.toFile().exists());
			//
			try {
				ValidateUtils.validateDirectory("name", directory);				
			} catch (ValidationException e) {
				fail(e.getMessage());
			}
			//
			try {
				ValidateUtils.validateDirectory("name", file);
				fail();
			} catch (ValidationException e) {				
			} 
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			directory.toFile().delete();
			file.toFile().delete();			
		}
	}

	@Test
	public void testValidateDirectoryStringFile() {
		File directory = null;
		File file = null;
		try {
			try {
				ValidateUtils.validateDirectory("name", directory);
				fail();
			} catch (ValidationException e) {			
			}
			try {
				ValidateUtils.validateDirectory("name", new File("slædfksdflæksædlfksælfksdlæ"));
				fail();
			} catch (ValidationException e) {			
			}
			//
			directory = Files.createTempDirectory("bente").toFile();
			file = Files.createTempFile("hans", ".txt").toFile();
			assertTrue(directory.exists());
			assertTrue(file.exists());
			//
			try {
				ValidateUtils.validateDirectory("name", directory);				
			} catch (ValidationException e) {
				fail(e.getMessage());
			}
			//
			try {
				ValidateUtils.validateDirectory("name", file);
				fail();
			} catch (ValidationException e) {				
			} 			
		} catch (IOException e) {
			fail(e.getMessage());			
		} finally {
			directory.delete();
			file.delete();			
		}
	}

	@Test
	public void testValidateFileStringPath() {
		Path directory = null;
		Path file = null;
		try {
			try {
				ValidateUtils.validateFile("name", file);
				fail();
			} catch (ValidationException e) {			
			}
			try {
				ValidateUtils.validateFile("name", new File("slædfksdflæksædlfksælfksdlæ").toPath());
				fail();
			} catch (ValidationException e) {			
			}
			//
			directory = Files.createTempDirectory("bente");
			file = Files.createTempFile("hans", ".txt");
			assertTrue(directory.toFile().exists());
			assertTrue(file.toFile().exists());
			//
			try {
				ValidateUtils.validateFile("name", directory);
				fail();
			} catch (ValidationException e) {				
			} 	
			//
			try {
				ValidateUtils.validateFile("name", file);				
			} catch (ValidationException e) {
				fail(e.getMessage());
			}
		
		} catch (IOException e) {
			fail(e.getMessage());			
		} finally {
			directory.toFile().delete();
			file.toFile().delete();			
		}
	}
	@Test
	public void testValidateFileStringFile() {
		File directory = null;
		File file = null;
		try {
			try {
				ValidateUtils.validateFile("name", file);
				fail();
			} catch (ValidationException e) {			
			}
			try {
				ValidateUtils.validateFile("name", new File("slædfksdflæksædlfksælfksdlæ"));
				fail();
			} catch (ValidationException e) {			
			}
			//
			directory = Files.createTempDirectory("bente").toFile();
			file = Files.createTempFile("hans", ".txt").toFile();
			assertTrue(directory.exists());
			assertTrue(file.exists());
			//
			try {
				ValidateUtils.validateFile("name", directory);
				fail();
			} catch (ValidationException e) {				
			} 	
			//
			try {
				ValidateUtils.validateFile("name", file);				
			} catch (ValidationException e) {
				fail(e.getMessage());
			}			
		} catch (IOException e) {
			fail(e.getMessage());			
		} finally {
			directory.delete();
			file.delete();			
		}
	}

	
	@Test
	public void testValidateNotNullNotChecked() {
		try {
			ValidateUtils.validateNotNullNotChecked("name", "hest");			
		} catch (NullPointerException e) {
			fail();
		}
		try {
			ValidateUtils.validateNotNullNotChecked("name", null);
			fail();
		} catch (NullPointerException e) {			
		}
	}
	@Test
	public void testValidateRangeTimespan() {
		TimeSpan value = null;
		TimeSpan minValue = null;
		TimeSpan maxValue = null;
		//
		try {
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			value=TimeSpan.createHours(8);
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=TimeSpan.createHours(6);
			value=TimeSpan.createHours(8);
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=TimeSpan.createHours(6);
			value=TimeSpan.createHours(8);
			maxValue=TimeSpan.createHours(10);			
			ValidateUtils.validateRange("name", value, minValue, maxValue);			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
		//
		try {
			minValue=TimeSpan.createHours(9);
			value=TimeSpan.createHours(8);
			maxValue=TimeSpan.createHours(10);			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}
		//
		try {
			minValue=TimeSpan.createHours(5);
			value=TimeSpan.createHours(8);
			maxValue=TimeSpan.createHours(7);			
			ValidateUtils.validateRange("name", value, minValue, maxValue);
			fail();
		} catch (ValidationException e) {			
		}		
	}
	@Test
	public void testValidateFailed() {
		try {
			ValidateUtils.validateFailed("name", new IllegalArgumentException("hest"));
			fail();
		} catch (ValidationException e) {
			assertNotNull(e);
			assertEquals(ValidationException.class, e.getClass());
		}
	}
	

}
