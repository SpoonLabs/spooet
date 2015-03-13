package spoon.test.spooet;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import spoon.SpooetFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import javax.lang.model.element.Modifier;

/**
 * Created by nicolas on 13/03/2015.
 */
public class MyProcessor extends AbstractProcessor<CtInterface<?>> {

	@Override
	public void process(CtInterface<?> element) {
		TypeSpec.Builder builder = TypeSpec.classBuilder(element.getSimpleName() + "Impl")
				.addModifiers(Modifier.PUBLIC)
				.addSuperinterface(TypeVariableName.get(element.getQualifiedName()));
		for (CtMethod<?> ctMethod : element.getMethods()) {
			MethodSpec.Builder mbuilder = MethodSpec
					.methodBuilder(ctMethod.getSimpleName())
					.addModifiers(Modifier.PUBLIC)
					.returns(TypeName.INT);

			for (CtParameter<?> ctParameter : ctMethod.getParameters()) {
				mbuilder.addParameter(ctParameter.getType().getActualClass(), ctParameter.getSimpleName());
			}
			for (CtTypeReference<? extends Throwable> ex : ctMethod.getThrownTypes()) {
				mbuilder.addException(ex.getActualClass());
			}
			mbuilder.addCode(CodeBlock.builder()
					.addStatement("int i = 5")
					.beginControlFlow("try")
					.addStatement("java.util.List myList")
					.nextControlFlow("catch (RuntimeException ex)")
					.addStatement("ex.printStackTrace()")
					.endControlFlow()
					.addStatement("return i").build());
			builder.addMethod(mbuilder.build());
		}

		new SpooetFactory(getFactory()).addToModel(element.getPackage().getQualifiedName(), builder.build());
	}
}
