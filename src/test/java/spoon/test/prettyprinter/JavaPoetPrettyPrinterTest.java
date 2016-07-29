package spoon.test.prettyprinter;

import org.junit.Assert;
import org.junit.Test;
import spoon.PoetLauncher;

public class JavaPoetPrettyPrinterTest {
	@Test
	public void testSampleCode() throws Exception {
		final PoetLauncher launcher = new PoetLauncher();
		launcher.addInputResource("src/test/java/sample");
		launcher.setSourceOutputDirectory("target/spooned");
		launcher.run();

		Assert.assertTrue(launcher.getModelBuilder().compile());
	}
}
