package de.bitdroid.jaxrs2retrofit;


import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import de.bitdroid.jaxrs2retrofit.converter.ParamConverterManager;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public final class MainTest {

	private static final String DUMMY_SOURCE_FILENAME = MainTest.class.getSimpleName() + "-DUMMY_SOURCE";

	@Capturing
	private RetrofitGenerator generator;
	@Capturing private JavaProjectBuilder projectBuilder;
	@Mocked private JavaClass javaClass;

	private BufferedReader stdOutReader;
	private PrintStream stdOut; // store locally for later undo

	@Before
	public void setupOutStream() throws Exception {
		PipedInputStream pipeInput = new PipedInputStream();
		stdOutReader = new BufferedReader(new InputStreamReader(pipeInput));
		stdOut = System.out;
		System.setOut(new PrintStream(new PipedOutputStream(pipeInput)));
	}


	@After
	public void resetOutStream() {
		System.setOut(stdOut);
	}


	@Before
	public void setupSrcFile() throws Exception {
		new File(DUMMY_SOURCE_FILENAME).createNewFile();
	}


	@After
	public void removeSrcFile() {
		new File(DUMMY_SOURCE_FILENAME).deleteOnExit();
	}


	@Test
	public void testMissingSource() throws Exception {
		Main.main(new String[]{});

		// check for help message
		Assert.assertTrue(stdOutReader.read() != -1);
		new Verifications() {{
			generator.createResource((JavaClass) any); times = 0;
		}};
	}


	@Test
	public void testConversion() throws Exception {
		final String excludeRegex = "excludeMe";

		new Expectations() {{
			projectBuilder.getClasses(); result = Arrays.asList(javaClass);
		}};

		Main.main(new String[] { "-src", DUMMY_SOURCE_FILENAME, "-exclude", excludeRegex });

		new Verifications() {{
			new RetrofitGenerator(RetrofitReturnStrategy.BOTH, anyString, excludeRegex, (ParamConverterManager) any);
			generator.createResource(javaClass);
		}};

	}

}
