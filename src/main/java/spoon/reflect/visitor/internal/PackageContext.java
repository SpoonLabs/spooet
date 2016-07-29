package spoon.reflect.visitor.internal;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import spoon.reflect.declaration.CtType;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by nicolas on 24/02/2015.
 */
public class PackageContext extends AbstractContext {

	private final String packageName;
	private TypeSpec.Builder type;

	public PackageContext(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public void addType(TypeSpec.Builder typeSpec, CtType<?> type) {
		this.type = typeSpec;
	}

	@Override
	public String toString() {
		if (type == null) {
			return null;
		}

		JavaFile javaFile = JavaFile.builder(packageName, type.build()).build();
		StringWriter writer = new StringWriter();
		try {
			javaFile.writeTo(writer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return writer.toString();
	}
}
