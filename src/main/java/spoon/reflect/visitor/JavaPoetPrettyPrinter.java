package spoon.reflect.visitor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.internal.CodePrinter;
import spoon.reflect.visitor.internal.Context;
import spoon.reflect.visitor.internal.ExecutableContext;
import spoon.reflect.visitor.internal.ModifiersUtils;
import spoon.reflect.visitor.internal.PackageContext;
import spoon.reflect.visitor.internal.TypeContext;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class JavaPoetPrettyPrinter extends CtScanner implements CtVisitor, PrettyPrinter {

	private Stack<Context> contexts = new Stack<>();

	private final DefaultJavaPrettyPrinter stmtprinter;

	public JavaPoetPrettyPrinter(DefaultJavaPrettyPrinter stmtprinter) {
		this.stmtprinter = stmtprinter;
	}

	private void enter(Context context) {
		contexts.push(context);
	}

	private <T extends Context> T exit() {
		return (T) contexts.pop();
	}

	private <T extends TypeName> T getTypeName(CtTypeReference<?> ref) {
		// TODO replace this method by a reference visitor
		if (ref.isPrimitive()) {
			if ("void".equals(ref.getSimpleName())) {
				return (T) TypeName.VOID;
			}
			if ("boolean".equals(ref.getSimpleName())) {
				return (T) TypeName.BOOLEAN;
			}
			if ("byte".equals(ref.getSimpleName())) {
				return (T) TypeName.BYTE;
			}
			if ("short".equals(ref.getSimpleName())) {
				return (T) TypeName.SHORT;
			}
			if ("int".equals(ref.getSimpleName())) {
				return (T) TypeName.INT;
			}
			if ("long".equals(ref.getSimpleName())) {
				return (T) TypeName.LONG;
			}
			if ("char".equals(ref.getSimpleName())) {
				return (T) TypeName.CHAR;
			}
			if ("float".equals(ref.getSimpleName())) {
				return (T) TypeName.FLOAT;
			}
			if ("double".equals(ref.getSimpleName())) {
				return (T) TypeName.DOUBLE;
			}
		} else if (ref instanceof CtArrayTypeReference) {
			return (T) ArrayTypeName.of(getTypeName(((CtArrayTypeReference) ref).getComponentType()));
		} else if (ref instanceof CtTypeParameterReference) {
			List<TypeName> bounds = new ArrayList<>();
			CtTypeParameterReference reference = (CtTypeParameterReference) ref;

			for (CtTypeReference<?> ctTypeReference : reference.getBounds()) {
				bounds.add(getTypeName(ctTypeReference));
			}
			if ("?".equals(reference.getSimpleName())) {
				if (bounds.isEmpty()) {
					bounds.add(TypeName.get(Object.class));
				}
				// TODO add multiple bounds
				if (reference.isUpper()) {
					return (T) WildcardTypeName.subtypeOf(bounds.get(0));
				} else {
					return (T) WildcardTypeName.supertypeOf(bounds.get(0));
				}
			}
			return (T) TypeVariableName.get(reference.getSimpleName(), bounds.toArray(new TypeName[bounds.size()]));
		} else if (ref.getActualTypeArguments().isEmpty()) {
			return (T) ClassName.get(ref.getActualClass());
		} else {
			Collection<TypeName> parameters = new ArrayList<>();
			for (CtTypeReference<?> ctTypeReference : ref.getActualTypeArguments()) {
				parameters.add(getTypeName(ctTypeReference));
			}
			return (T) ParameterizedTypeName.get(ClassName.get(ref.getActualClass()), parameters.toArray(new TypeName[parameters.size()]));
		}
		throw new UnsupportedOperationException();
	}

	private TypeSpec.Builder createType(CtType<?> type, TypeSpec.Builder builder) {
		builder.addModifiers(ModifiersUtils.getReflect(type.getModifiers()));
		for (CtTypeReference<?> ctTypeReference : type.getFormalTypeParameters()) {
			TypeVariableName var = getTypeName(ctTypeReference);
			builder.addTypeVariable(var);
		}
		for (CtTypeReference intf : type.getSuperInterfaces()) {
			builder.addSuperinterface(getTypeName(intf));
		}
		return builder;
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		final TypeSpec.Builder builder = createType(ctClass, TypeSpec.classBuilder(ctClass.getSimpleName()));
		if (ctClass.getSuperclass() != null) {
			builder.superclass(getTypeName(ctClass.getSuperclass()));
		}

		enter(new TypeContext(builder));
		super.visitCtClass(ctClass);
		exit();

		contexts.peek().addType(builder, ctClass);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> ctInterface) {
		final TypeSpec.Builder builder = createType(ctInterface, TypeSpec.interfaceBuilder(ctInterface.getSimpleName()));
		enter(new TypeContext(builder) {

			@Override
			public void addMethod(MethodSpec.Builder methodSpec, CtExecutable method) {
				methodSpec.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
				super.addMethod(methodSpec, method);
			}

			@Override
			public void addField(FieldSpec.Builder fieldSpec, CtField field) {
				fieldSpec.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
				super.addField(fieldSpec, field);
			}
		});
		super.visitCtInterface(ctInterface);
		exit();
		contexts.peek().addType(builder, ctInterface);
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
		FieldSpec.Builder field = FieldSpec.builder(getTypeName(f.getType()), f.getSimpleName()).addModifiers(ModifiersUtils.getReflect(f.getModifiers()));

		stmtprinter.reset();
		stmtprinter.scan(f.getDefaultExpression());
		String result = stmtprinter.getResult().replaceAll("\\$", "\\$\\$");
		field.initializer(result);
		contexts.peek().addField(field, f);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		final TypeSpec.Builder builder = TypeSpec.enumBuilder(ctEnum.getSimpleName());
		builder.addModifiers(ModifiersUtils.getReflect(ctEnum.getModifiers()));

		enter(new TypeContext(builder));
		scan(ctEnum.getAnnotations());
		for (CtField f : ctEnum.getFields()) {
			if (f instanceof CtEnumValue<?>) {
				builder.addEnumConstant(f.getSimpleName());
			} else {
				scan(f);
			}
		}
		scan(ctEnum.getConstructors());
		scan(ctEnum.getMethods());
		scan(ctEnum.getNestedTypes());
		exit();
		contexts.peek().addType(builder, ctEnum);
	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
		TypeSpec.Builder builder = TypeSpec.annotationBuilder(annotationType.getSimpleName());
		builder.addModifiers(ModifiersUtils.getReflect(annotationType.getModifiers()));
		enter(new TypeContext(builder));

		scan(annotationType.getAnnotations());
		scan(annotationType.getNestedTypes());
		// scan(annotationType.getFields());
		for (CtField<?> ctField : annotationType.getFields()) {
			MethodSpec.Builder mbuilder = MethodSpec //
					.methodBuilder(ctField.getSimpleName()) //
					.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
					.defaultValue(ctField.getDefaultExpression().toString()) //
					.returns(getTypeName(ctField.getType()));
			builder.addMethod(mbuilder.build());
		}

		exit();
		contexts.peek().addType(builder, annotationType);
	}

	private MethodSpec.Builder visitMethod(MethodSpec.Builder builder, CtMethod<?> executable) {
		builder.addModifiers(ModifiersUtils.getReflect(executable.getModifiers()));
		return visitExecutable(builder, executable);
	}

	private MethodSpec.Builder visitConstructor(MethodSpec.Builder builder, CtConstructor<?> executable) {
		builder.addModifiers(ModifiersUtils.getReflect(executable.getModifiers()));
		return visitExecutable(builder, executable);
	}

	private MethodSpec.Builder visitExecutable(MethodSpec.Builder builder, CtExecutable<?> executable) {
		for (CtTypeReference<? extends Throwable> ctTypeReference : executable.getThrownTypes()) {
			builder.addException(getTypeName(ctTypeReference));
		}
		if (executable.getBody() != null) {
			CodePrinter printer = new CodePrinter(builder, executable.getParent(CtType.class), stmtprinter);

			for (CtStatement statement : executable.getBody().getStatements()) {
				printer.scan(statement);
			}
		}
		return builder;
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		final MethodSpec.Builder methodSpec = MethodSpec.constructorBuilder();
		visitConstructor(methodSpec, c);
		enter(new ExecutableContext(methodSpec));
		super.visitCtConstructor(c);
		exit();
		contexts.peek().addMethod(methodSpec, c);
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		final MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(m.getSimpleName()).returns(getTypeName(m.getType()));
		visitMethod(methodSpec, m);
		for (CtTypeReference<?> ctTypeReference : m.getFormalTypeParameters()) {
			methodSpec.addTypeVariable((TypeVariableName) getTypeName(ctTypeReference));
		}
		enter(new ExecutableContext(methodSpec));
		scan(m.getParameters());
		scan(m.getAnnotations());
		exit();

		contexts.peek().addMethod(methodSpec, m);
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		ParameterSpec.Builder param = ParameterSpec.builder(getTypeName(parameter.getType()), parameter.getSimpleName()).addModifiers(ModifiersUtils.getReflect(parameter.getModifiers()));

		super.visitCtParameter(parameter);
		contexts.peek().addParameter(param, parameter.isVarArgs(), parameter);
	}

	@Override
	public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder((ClassName) getTypeName(annotation.getAnnotationType()));

		for (Map.Entry<String, Object> stringObjectEntry : annotation.getElementValues().entrySet()) {
			annotationSpec.addMember(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
		}

		contexts.peek().addAnnotation(annotationSpec, annotation);

	}

	@Override
	public String getPackageDeclaration() {
		return null;
	}

	@Override
	public String getResult() {
		return exit().toString();
	}

	@Override
	public void reset() {
		contexts.clear();
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		for (CtType<?> type : types) {
			if (contexts.isEmpty()) {
				enter(new PackageContext(CtPackage.TOP_LEVEL_PACKAGE_NAME.equals(type.getPackage().getSimpleName()) ? "" : type.getPackage().getQualifiedName()));
			}
			scan(type);
		}
	}

	@Override
	public Map<Integer, Integer> getLineNumberMapping() {
		return null;
	}

}
