package spoon.reflect.visitor.internal;

import com.squareup.javapoet.MethodSpec;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

/**
 * Created by nicolas on 24/02/2015.
 */
public class CodePrinter extends CtInheritanceScanner {

	final MethodSpec.Builder currentMethod;
	final CtSimpleType currentThis;
	final DefaultJavaPrettyPrinter defaultJavaPrettyPrinter;

	public CodePrinter(MethodSpec.Builder currentMethod, CtSimpleType currentThis,
			DefaultJavaPrettyPrinter defaultJavaPrettyPrinter) {
		this.currentMethod = currentMethod;
		this.currentThis = currentThis;

		this.defaultJavaPrettyPrinter = defaultJavaPrettyPrinter;
	}

	private String codeToString(CtElement element) {
		defaultJavaPrettyPrinter.reset();
		//	defaultJavaPrettyPrinter.getContext().currentThis.push(currentThis.getReference());
		defaultJavaPrettyPrinter.scan(element);
		//	defaultJavaPrettyPrinter.getContext().currentThis.pop();
		return defaultJavaPrettyPrinter.getResult().replaceAll("\\$", "\\$\\$");
	}

	@Override
	public void scanCtStatement(CtStatement s) {
		if (!s.isImplicit()) {
			currentMethod.addCode(codeToString(s) + ";\n");
		}
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
		currentMethod.beginControlFlow("try");

		scan(tryBlock.getBody());

		for (CtCatch ctCatch : tryBlock.getCatchers()) {
			currentMethod.nextControlFlow("catch (" + ctCatch.getParameter().toString() + ")");
			scan(ctCatch.getBody());
		}

		if (tryBlock.getFinalizer() != null) {
			currentMethod.nextControlFlow("finally");
			scan(tryBlock.getFinalizer());
		}

		currentMethod.endControlFlow();
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		currentMethod.beginControlFlow("if (" + ifElement.getCondition().toString() + ")");
		scan(ifElement.getThenStatement());
		if (ifElement.getElseStatement() != null) {
			currentMethod.nextControlFlow("else ");
			scan(ifElement.getElseStatement());
		}
		currentMethod.endControlFlow();
	}

	@Override
	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		currentMethod.beginControlFlow("switch (" + switchStatement.getSelector() + ")");
		scan(switchStatement.getCases());
		currentMethod.endControlFlow();
	}

	@Override
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		currentMethod.addCode(codeToString(caseStatement) + "\n");
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		for (CtStatement statement : block.getStatements()) {
			scan(statement);
		}
	}
}
