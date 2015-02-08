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
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
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
		System.out.println(builder.getClassByName("org.jvalue.ods.rest.AbstractApi").getName());
		for (JavaPackage p : builder.getPackages()) System.out.println(p.getName());

		for (JavaClass javaClass : builder.getClasses()) {
			JavaFile javaFile = generateClientForClass(javaClass);
			if (javaFile == null) continue;
			javaFile.writeTo(System.out);
		}
	}


	private static JavaFile generateClientForClass(JavaClass inputClass) {
		System.out.println("Generating " + inputClass.getName());

		// find path annotation
		JavaAnnotation path = null;
		for (JavaAnnotation annotation : inputClass.getAnnotations()) {
			if (annotation.getType().getFullyQualifiedName().equals(Path.class.getName())) {
				path = annotation;
				break;
			}
		}
		if (path == null) return null;

		TypeSpec.Builder resourceBuilder = TypeSpec.interfaceBuilder(inputClass.getName())
				.addModifiers(Modifier.PUBLIC);

		for (JavaMethod method : inputClass.getMethods()) {
			MethodSpec.Builder methodBuilder =  MethodSpec
					.methodBuilder(method.getName())
					.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

			// find method type and path
			JavaAnnotation methodPath = null;
			HttpMethod httpMethod = null;
			for (JavaAnnotation annotation : method.getAnnotations()) {
				if (annotation.getType().getFullyQualifiedName().equals(Path.class.getName())) {
					methodPath = annotation;
				} else {
					for (HttpMethod m : HttpMethod.values()) {
						if (m.getJaxRsClass().getName().equals(annotation.getType().getFullyQualifiedName())) {
							httpMethod = m;
							break;
						}
					}
				}
			}
			if (httpMethod == null) continue;
			methodBuilder.addAnnotation(createPathAnnotation(httpMethod, path, methodPath));

			// parse parameters
			for (JavaParameter parameter : method.getParameters()) {
				JavaAnnotation paramAnnotation = null;
				ParameterType parameterType = null;

				for (JavaAnnotation annotation : parameter.getAnnotations()) {
					for (ParameterType type : ParameterType.values()) {
						if (type.getJaxRsClass() == null) continue;
						if (annotation.getType().getFullyQualifiedName().equals(type.getJaxRsClass().getName())) {
							paramAnnotation =  annotation;
							parameterType = type;
							break;
						}
					}
					if (parameterType == null) parameterType = ParameterType.BODY;

					ClassName paramClassName = ClassName.bestGuess(parameter.getJavaClass().getFullyQualifiedName());
					ParameterSpec.Builder paramBuilder = ParameterSpec.builder(paramClassName, parameter.getName());

					AnnotationSpec.Builder paramAnnotationBuilder = AnnotationSpec.builder(parameterType.getRetrofitClass());
					if (paramAnnotation != null) paramAnnotationBuilder.addMember("value", paramAnnotation.getNamedParameter("value").toString());
					paramBuilder.addAnnotation(paramAnnotationBuilder.build());

					methodBuilder.addParameter(paramBuilder.build());
				}
			}

			// create return type
			String returnType = method.getReturnType().getFullyQualifiedName();
			TypeName retrofitReturnType;
			if ("void".equals(returnType)) retrofitReturnType = TypeName.VOID;
			else retrofitReturnType = ClassName.bestGuess(method.getReturnType().getFullyQualifiedName());
			methodBuilder.returns(retrofitReturnType);

			resourceBuilder.addMethod(methodBuilder.build());
		}

		return JavaFile.builder(inputClass.getPackageName(), resourceBuilder.build()).build();
	}


	private static AnnotationSpec createPathAnnotation(HttpMethod method, JavaAnnotation classPath, JavaAnnotation methodPath) {
		AnnotationValue pathExpression = classPath.getProperty("value");
		if (methodPath != null) {
			pathExpression = new Add(pathExpression, methodPath.getProperty("value"));
		}
		// TODO evaluation still failing with unknown ref values
		return AnnotationSpec.builder(method.getRetrofitClass())
				.addMember("value", "\"" + pathExpression.accept(new SimpleEvaluatingVisitor()).toString() + "\"")
				.build();
	}


	private static void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(Main.class.getSimpleName(), commandLineOptions);
	}


	private static class SimpleEvaluatingVisitor extends EvaluatingVisitor {

		@Override
		public Object getFieldReferenceValue(JavaField field) {
			// TODO
			return field.getDeclaringClass().getFieldByName(field.getName());
		}

	}

}
