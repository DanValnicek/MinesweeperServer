package test.com.company;

import Game.JsonGenerator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static com.company.MessageTypes.i;

/**
 * JsonGenerator Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>01/06/2022</pre>
 */
public class JsonGeneratorTest extends TestCase {
	public JsonGeneratorTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(JsonGeneratorTest.class);
	}

	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Method: createCallback(MessageTypes type, Object message)
	 */
	public void testCreateCallback() throws Exception {
//TODO: Test goes here...
		System.out.println("idk");
		System.out.println("idk2");
		JsonGenerator.createCallback(i, "idk");
	}
} 
