package spoon.reflect.visitor.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;

import java.lang.annotation.Annotation;

/**
 * Created by nicolas on 24/02/2015.
 */
public abstract class AbstractContext implements Context {
	@Override
	public void addType(TypeSpec.Builder typeSpec, CtSimpleType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMethod(MethodSpec.Builder methodSpec, CtExecutable method) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addField(FieldSpec.Builder fieldSpec, CtField field) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addParameter(ParameterSpec.Builder param, boolean varargs, CtParameter parameter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A extends Annotation> void addAnnotation(AnnotationSpec.Builder annotationSpec,
			CtAnnotation<A> annotation) {
		throw new UnsupportedOperationException();
	}
}
