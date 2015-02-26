package spoon.test.prettyprinter;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.JavaPoetPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import utils.TestSpooner;

import java.io.File;
import java.lang.reflect.Method;

public class JavaPoetPrettyPrinterTest {
	
	
	@Test
	public void testSampleCode() throws Exception {
		TestSpooner spooner = new TestSpooner();

		spooner.getFactory().getEnvironment()
				.setDefaultFileGenerator(new JavaOutputProcessor(new File("build/spooned"), new JavaPoetPrettyPrinter(
						new DefaultJavaPrettyPrinter(spooner.getFactory().getEnvironment()))));

		spooner.addSource(new File("src/test/java/sample"));
		spooner.process();
		spooner.print(new File("target/spooned"));
		Assert.assertTrue(spooner.compile());
	}

}
