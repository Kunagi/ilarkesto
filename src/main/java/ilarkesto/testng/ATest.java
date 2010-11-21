package ilarkesto.testng;

import org.testng.Assert;

public class ATest extends Assert {

	public static final String OUTPUT_DIR = "test-output";

	public static void assertStartsWith(String actual, String expectedPrefix) {
		assertTrue(actual.startsWith(expectedPrefix), "<" + actual + "> expected to start with <" + expectedPrefix
				+ "> |");
	}

}
