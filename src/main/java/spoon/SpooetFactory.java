package spoon;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeWriter;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.internal.ModifiersUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SpooetFactory {

	private final Factory factory;

	public SpooetFactory(Factory factory) {
		this.factory = factory;
	}

	public void addToModel(String pack, TypeSpec type) {
		factory.Package().getOrCreate(pack).addType(translate(type));
	}

	private CtSimpleType create(TypeSpec.Kind kind) {
		switch (kind) {
		case INTERFACE:
			return factory.Core().createInterface();
		case CLASS:
			return factory.Core().createClass();
		case ANNOTATION:
			return factory.Core().createAnnotationType();
		case ENUM:
			return factory.Core().createEnum();
		}
		throw new IllegalArgumentException("unknown kind");
	}

	public <T> CtSimpleType<T> translate(final TypeSpec typeSpec) {
		final CtSimpleType<T> type = create(typeSpec.kind);

		new CtInheritanceScanner() {
			@Override
			public <T> void scanCtSimpleType(CtSimpleType<T> t) {
				super.scanCtSimpleType(t);
				t.setSimpleName(typeSpec.name);
				for (AnnotationSpec annotation : typeSpec.annotations) {
					t.addAnnotation(translate(annotation));
				}
				t.setModifiers(translate(typeSpec.modifiers));
				t.setDocComment(typeSpec.javadoc.toString());
				for (TypeSpec spec : typeSpec.typeSpecs) {
					t.addNestedType(translate(spec));
				}

			}

			@Override
			public <T> void scanCtType(CtType<T> type) {
				super.scanCtType(type);
				for (TypeVariableName typeVariable : typeSpec.typeVariables) {
					type.addFormalTypeParameter(translate(typeVariable));
				}
				for (TypeName superinterface : typeSpec.superinterfaces) {
					type.addSuperInterface(translate(superinterface));
				}
				for (FieldSpec fieldSpec : typeSpec.fieldSpecs) {
					type.addField(translate(fieldSpec));
				}
				for (MethodSpec methodSpec : typeSpec.methodSpecs) {
					type.addMethod(translate(methodSpec));
				}
			}

			@Override
			public <T extends Enum<?>> void visitCtEnum(CtEnum<T> e) {
				super.visitCtEnum(e);
				for (Map.Entry<String, TypeSpec> stringTypeSpecEntry : typeSpec.enumConstants.entrySet()) {
					throw new UnsupportedOperationException("not yet implemented");
				}
			}

			@Override
			public <T> void visitCtClass(CtClass<T> e) {
				super.visitCtClass(e);
				e.setSuperclass(translate(typeSpec.superclass));
			}

		}.scan(type);

		return type;
	}

	public <T> CtMethod<T> translate(MethodSpec method) {
		CtMethod<T> ctmethod = factory.Core().createMethod();

		ctmethod.setSimpleName(method.name);
		ctmethod.setDocComment(method.javadoc.toString());
		for (AnnotationSpec annotation : method.annotations) {
			ctmethod.addAnnotation(translate(annotation));
		}
		ctmethod.setModifiers(translate(method.modifiers));
		for (TypeVariableName typeVariable : method.typeVariables) {
			ctmethod.addFormalTypeParameter(translate(typeVariable));
		}
		ctmethod.setType((CtTypeReference<T>) translate(method.returnType));
		for (Iterator<ParameterSpec> iterator = method.parameters.iterator(); iterator.hasNext(); ) {
			ParameterSpec parameter = iterator.next();
			CtParameter param = translate(parameter);
			if (!iterator.hasNext()) {
				param.setVarArgs(method.varargs);
			}
			ctmethod.addParameter(param);
		}

		for (TypeName exception : method.exceptions) {
			ctmethod.addThrownType((CtTypeReference) translate(exception));
		}
		ctmethod.setBody((CtBlock<T>) translate(method.code));
		return ctmethod;
	}

	public <T> CtParameter<T> translate(ParameterSpec param) {
		CtParameter parameter = factory.Core().createParameter();

		parameter.setSimpleName(param.name);
		for (AnnotationSpec annotation : param.annotations) {
			parameter.addAnnotation(translate(annotation));
		}
		parameter.setModifiers(translate(param.modifiers));
		parameter.setType(translate(param.type));

		return parameter;
	}

	public <A extends Annotation> CtAnnotation<A> translate(AnnotationSpec annotationSpec) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public <T> CtField<T> translate(FieldSpec fieldSpec) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public <T> CtBlock<T> translate(CodeBlock body) {
		CtBlock ctBlock = factory.Core().createBlock();

		StringWriter out = new StringWriter();
		try {
			new CodeWriter(out, "\t").emit(body);
		} catch (IOException e) {
			throw new AssertionError();
		}
		ctBlock.addStatement(factory.Code().createCodeSnippetStatement(out.toString()));
		return ctBlock;
	}

	public <T> CtTypeReference<T> translate(TypeName type) {
		return factory.Type().createReference(type.toString());
	}

	public Set<ModifierKind> translate(Set<Modifier> modifiers) {
		Set<ModifierKind> modifierKinds = new HashSet<>();
		for (Modifier modifier : modifiers) {
			modifierKinds.add(ModifiersUtils.getModifierKind(modifier));
		}
		return modifierKinds;
	}

}
