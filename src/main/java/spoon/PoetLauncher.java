package spoon;

import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.JavaPoetPrettyPrinter;
import spoon.reflect.visitor.PrettyPrinter;

/**
 * Created by nicolas on 25/02/2015.
 */
public class PoetLauncher extends Launcher {

	public static void main(String[] args) throws Exception {
		Launcher launcher = new PoetLauncher();
		launcher.setArgs(args);
		if (args.length != 0) {
			launcher.run();
		} else {
			launcher.printUsage();
		}
	}

	@Override
	public PrettyPrinter createPrettyPrinter(Environment environment) {
		return new JavaPoetPrettyPrinter(new DefaultJavaPrettyPrinter(environment));
	}
}
