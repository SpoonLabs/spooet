package spoon.reflect.visitor.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;

import java.lang.annotation.Annotation;

/**
 * Created by nicolas on 24/02/2015.
 */
public class TypeContext extends AbstractContext {
	private TypeSpec.Builder builder;

	public TypeContext(TypeSpec.Builder builder) {
		this.builder = builder;
	}

	@Override
	public void addType(TypeSpec.Builder typeSpec, CtType<?> type) {
		builder.addType(typeSpec.build());
	}

	@Override
	public void addMethod(MethodSpec.Builder methodSpec, CtExecutable method) {
		builder.addMethod(methodSpec.build());
	}

	@Override
	public void addField(FieldSpec.Builder fieldSpec, CtField field) {
		builder.addField(fieldSpec.build());
	}

	@Override
	public <A extends Annotation> void addAnnotation(AnnotationSpec.Builder annotationSpec, CtAnnotation<A> annotation) {
		builder.addAnnotation(annotationSpec.build());
	}
}