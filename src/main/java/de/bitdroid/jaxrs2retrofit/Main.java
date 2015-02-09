package de.bitdroid.jaxrs2retrofit;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.builder.impl.EvaluatingVisitor;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.File;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Path;

public final class Main {

	private static final Options commandLineOptions = new Options();
	private static final String OPTION_SOURCE = "src";

	static {
		commandLineOptions.addOption(OPTION_SOURCE, true, "JAX RS Java input files");
	}


	public static void main(String[] args) throws Exception {
		CommandLine commandLine = new BasicParser().parse(commandLineOptions, args);
		if (!commandLine.hasOption(OPTION_SOURCE)) {
			printHelp();
			return;
		}

		File inputFile = new File(commandLine.getOptionValue(OPTION_SOURCE));
		if (!inputFile.exists()) {
			printHelp();
			return;
		}

		JavaProjectBuilder builder = new JavaProjectBuilder();
		builder.addSourceTree(inputFile);

		for (JavaClass javaClass : builder.getClasses()) {
			JavaFile javaFile = generateResource(javaClass);
			if (javaFile == null) continue;
			javaFile.writeTo(System.out);
		}
	}


	private static JavaFile generateResource(JavaClass jaxRsClass) {
		// find path annotation
		JavaAnnotation jaxRsPath = null;
		for (JavaAnnotation annotation : jaxRsClass.getAnnotations()) {
			if (annotation.getType().getFullyQualifiedName().equals(Path.class.getName())) {
				jaxRsPath = annotation;
				break;
			}
		}
		if (jaxRsPath == null) return null; // no a valid JAX RS resource

		TypeSpec.Builder retrofitResourceBuilder = TypeSpec
				.interfaceBuilder(jaxRsClass.getName())
				.addModifiers(Modifier.PUBLIC);

		for (JavaMethod jaxRsMethod : jaxRsClass.getMethods()) {
			MethodSpec retrofitMethod = generateMethod(jaxRsClass, jaxRsMethod, jaxRsPath);
			if (retrofitMethod != null) retrofitResourceBuilder.addMethod(retrofitMethod);
		}

		return JavaFile.builder(jaxRsClass.getPackageName(), retrofitResourceBuilder.build()).build();
	}


	private static MethodSpec generateMethod(
			JavaClass jaxRsClass,
			JavaMethod jaxRsMethod,
			JavaAnnotation jaxRsPath) {

		MethodSpec.Builder retrofitMethodBuilder = MethodSpec
				.methodBuilder(jaxRsMethod.getName())
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

		// find method type and path
		JavaAnnotation jaxRsMethodPath = null;
		HttpMethod httpMethod = null;
		for (JavaAnnotation annotation : jaxRsMethod.getAnnotations()) {
			if (annotation.getType().getFullyQualifiedName().equals(Path.class.getName())) {
				jaxRsMethodPath = annotation;
			} else {
				httpMethod = HttpMethod.forJaxRsClassName(annotation.getType().getFullyQualifiedName());
			}
		}
		if (httpMethod == null) return null; // not a valid resource method
		retrofitMethodBuilder
				.addAnnotation(createPathAnnotation(jaxRsClass, httpMethod, jaxRsPath, jaxRsMethodPath));

		// create return type
		String jaxRsReturnType = jaxRsMethod.getReturnType().getFullyQualifiedName();
		TypeName retrofitReturnType;
		if ("void".equals(jaxRsReturnType)) {
			retrofitReturnType = TypeName.VOID;
		} else {
			retrofitReturnType = ClassName.bestGuess(jaxRsMethod.getReturnType().getFullyQualifiedName());
		}
		retrofitMethodBuilder.returns(retrofitReturnType);

		// create parameters
		for (JavaParameter jaxRsParameter : jaxRsMethod.getParameters()) {
			retrofitMethodBuilder.addParameter(createParameter(jaxRsParameter));
		}

		return retrofitMethodBuilder.build();
	}


	private static ParameterSpec createParameter(
			JavaParameter jaxRsParameter) {

		JavaAnnotation jaxRsAnnotation = null;
		ParameterType parameterType = null;

		for (JavaAnnotation annotation : jaxRsParameter.getAnnotations()) {
			parameterType = ParameterType.forJaxRsClassName(annotation.getType().getFullyQualifiedName());
			if (parameterType != null) {
				jaxRsAnnotation = annotation;
				break;
			}
		}
		if (parameterType == null) parameterType = ParameterType.BODY; // if none found assume that it belongs into the body

		ClassName retrofitParamClassName = ClassName
				.bestGuess(jaxRsParameter.getJavaClass().getFullyQualifiedName());
		ParameterSpec.Builder retrofitParamBuilder = ParameterSpec
				.builder(retrofitParamClassName, jaxRsParameter.getName());

		AnnotationSpec.Builder retrofitParamAnnotationBuilder = AnnotationSpec
				.builder(parameterType.getRetrofitClass());

		if (jaxRsAnnotation != null) {
			retrofitParamAnnotationBuilder.addMember("value", jaxRsAnnotation.getNamedParameter("value").toString());
		}
		retrofitParamBuilder.addAnnotation(retrofitParamAnnotationBuilder.build());
		return retrofitParamBuilder.build();
	}


	private static AnnotationSpec createPathAnnotation(JavaClass context, HttpMethod method, JavaAnnotation classPath, JavaAnnotation methodPath) {
		AnnotationValue pathExpression = classPath.getProperty("value");
		if (methodPath != null) {
			pathExpression = new Add(pathExpression, methodPath.getProperty("value"));
		}
		EvaluatingVisitor evaluatingVisitor = new SimpleEvaluatingVisitor(context);
		return AnnotationSpec.builder(method.getRetrofitClass())
				.addMember("value", "\"" + pathExpression.accept(evaluatingVisitor).toString() + "\"")
				.build();
	}


	private static void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(Main.class.getSimpleName(), commandLineOptions);
	}

}
