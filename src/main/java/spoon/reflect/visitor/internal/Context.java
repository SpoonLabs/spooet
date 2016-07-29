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
import spoon.reflect.declaration.CtType;

import java.lang.annotation.Annotation;

/**
 * Created by nicolas on 24/02/2015.
 */
public interface Context {
	void addType(TypeSpec.Builder typeSpec, CtType<?> type);

	void addMethod(MethodSpec.Builder methodSpec, CtExecutable<?> method);

	void addField(FieldSpec.Builder fieldSpec, CtField<?> field);

	void addParameter(ParameterSpec.Builder param, boolean varargs, CtParameter<?> parameter);

	<A extends Annotation> void addAnnotation(AnnotationSpec.Builder annotationSpec, CtAnnotation<A> annotation);
}
