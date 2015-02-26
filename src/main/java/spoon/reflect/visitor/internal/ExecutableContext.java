package spoon.reflect.visitor.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;

import java.lang.annotation.Annotation;

/**
 * Created by nicolas on 24/02/2015.
 */
public class ExecutableContext extends AbstractContext {

	private final MethodSpec.Builder methodSpec;

	public ExecutableContext(MethodSpec.Builder methodSpec) {
		this.methodSpec = methodSpec;
	}

	public MethodSpec getMethodSpec() {
		return methodSpec.build();
	}

	@Override
	public void addParameter(ParameterSpec.Builder param, boolean varargs, CtParameter parameter) {
		methodSpec.addParameter(param.build());
		if (varargs) methodSpec.varargs();
	}

	@Override
	public <A extends Annotation> void addAnnotation(AnnotationSpec.Builder annotationSpec,
			CtAnnotation<A> annotation) {
		methodSpec.addAnnotation(annotationSpec.build());
	}
}
