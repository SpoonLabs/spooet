package spoon.test.spooet;

import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.Arrays;

/**
 * Created by nicolas on 13/03/2015.
 */
public class SpooetTest {
	private Factory factory;

	@Test
	public void testSpooetBuilder() throws Throwable {
		final File testDirectory = new File("./src/test/java/spoon/test/spooet/test/");

		Launcher launcher = new Launcher();
		this.factory = launcher.createFactory();
		factory.getEnvironment().setDefaultFileGenerator(launcher.createOutputWriter(new File("spooned/"), factory.getEnvironment()));
		SpoonCompiler compiler = launcher.createCompiler(this.factory);

		compiler.setOutputDirectory(new File("spooned/"));
		compiler.addInputSource(testDirectory);
		compiler.build();
		compiler.process(Arrays.asList(MyProcessor.class.getName()));
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

	}

}
