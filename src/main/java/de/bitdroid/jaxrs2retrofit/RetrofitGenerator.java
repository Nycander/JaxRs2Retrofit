package de.bitdroid.jaxrs2retrofit;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.thoughtworks.qdox.builder.impl.EvaluatingVisitor;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Modifier;
import javax.ws.rs.Path;

import retrofit.Callback;
import retrofit.client.Response;

public final class RetrofitGenerator {

	private final RetrofitReturnStrategy retrofitReturnStrategy;
	private final String packageName;

	public RetrofitGenerator(RetrofitReturnStrategy retrofitReturnStrategy, String packageName) {
		this.retrofitReturnStrategy = retrofitReturnStrategy;
		this.packageName = packageName;
	}


	public JavaFile createResource(JavaClass jaxRsClass) {
		// find path annotation
		JavaAnnotation jaxRsPath = null;
		for (JavaAnnotation annotation : jaxRsClass.getAnnotations()) {
			if (annotation.getType().getFullyQualifiedName().equals(Path.class.getName())) {
				jaxRsPath = annotation;
				break;
			}
		}
		if (jaxRsPath == null) return null; // no a valid JAX RS resource

		System.out.println(jaxRsClass.getName());
		TypeSpec.Builder retrofitResourceBuilder = TypeSpec
				.interfaceBuilder(jaxRsClass.getName())
				.addModifiers(Modifier.PUBLIC);

		for (JavaMethod jaxRsMethod : jaxRsClass.getMethods()) {
			List<MethodSpec> retrofitMethods = createMethod(jaxRsClass, jaxRsMethod, jaxRsPath);
			if (retrofitMethods != null) {
				for (MethodSpec method : retrofitMethods) {
					retrofitResourceBuilder.addMethod(method);
				}
			}
		}

		return JavaFile.builder(packageName, retrofitResourceBuilder.build()).build();
	}


	private List<MethodSpec> createMethod(
			JavaClass jaxRsClass,
			JavaMethod jaxRsMethod,
			JavaAnnotation jaxRsPath) {

		List<MethodSpec> retrofitMethods = new LinkedList<>();
		MethodSpec.Builder retrofitMethodBuilder = MethodSpec
				.methodBuilder(jaxRsMethod.getName())
				.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

		// find method type and path
		JavaAnnotation jaxRsMethodPath = null;
		HttpMethod httpMethod = null;
		for (JavaAnnotation annotation : jaxRsMethod.getAnnotations()) {
			if (annotation.getType().getFullyQualifiedName().equals(Path.class.getName())) {
				jaxRsMethodPath = annotation;
			} else if (httpMethod == null) {
				httpMethod = HttpMethod.forJaxRsClassName(annotation.getType().getFullyQualifiedName());
			}
		}
		if (httpMethod == null) return null; // not a valid resource method
		retrofitMethodBuilder
				.addAnnotation(createPathAnnotation(jaxRsClass, httpMethod, jaxRsPath, jaxRsMethodPath));

		// create parameters
		for (JavaParameter jaxRsParameter : jaxRsMethod.getParameters()) {
			retrofitMethodBuilder.addParameter(createParameter(jaxRsParameter));
		}

		// create return type
		TypeName retrofitReturnType = createType(jaxRsMethod.getReturnType());
		if (retrofitReturnType.equals(TypeName.VOID)) {
			retrofitReturnType = ClassName.get(Response.class);
		}
		if (RetrofitReturnStrategy.REGULAR.equals(retrofitReturnStrategy) || RetrofitReturnStrategy.BOTH.equals(retrofitReturnStrategy)) {
			retrofitMethodBuilder.returns(retrofitReturnType);
			retrofitMethods.add(retrofitMethodBuilder.build());
		}
		if (RetrofitReturnStrategy.CALLBACK.equals(retrofitReturnStrategy) || RetrofitReturnStrategy.BOTH.equals(retrofitReturnStrategy)) {
			ParameterSpec callback = ParameterSpec
					.builder(ParameterizedTypeName.get(ClassName.get(Callback.class), retrofitReturnType), "callback")
					.build();
			retrofitMethodBuilder.addParameter(callback);
			retrofitMethodBuilder.returns(TypeName.VOID);
			retrofitMethods.add(retrofitMethodBuilder.build());
		}

		return retrofitMethods;
	}


	private ParameterSpec createParameter(
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

		TypeName retrofitParamClassName = createType(jaxRsParameter.getJavaClass());
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


	private final Pattern pathRegexPattern = Pattern.compile("\\{?(\\w+):*[.[^\\{\\}]]*\\}?");

	private AnnotationSpec createPathAnnotation(JavaClass context, HttpMethod method, JavaAnnotation classPath, JavaAnnotation methodPath) {
		AnnotationValue pathExpression = classPath.getProperty("value");
		if (methodPath != null) {
			pathExpression = new Add(pathExpression, methodPath.getProperty("value"));
		}
		EvaluatingVisitor evaluatingVisitor = new SimpleEvaluatingVisitor(context);
		String value =  pathExpression.accept(evaluatingVisitor).toString();
		Matcher matcher = pathRegexPattern.matcher(value);
		StringBuilder regexFreeValue = new StringBuilder();
		while (matcher.find()) {
			regexFreeValue.append("/");
			String regexValue = matcher.group(0);
			if (regexValue.startsWith("{")) regexFreeValue
					.append("{")
					.append(matcher.group(1))
					.append("}");
			else regexFreeValue.append(matcher.group(1));
		}

		return AnnotationSpec.builder(method.getRetrofitClass())
				.addMember("value", "\"" + regexFreeValue.toString() + "\"")
				.build();
	}


	private TypeName createType(JavaType jaxRsType) {
		if (void.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.VOID;

		} else if (boolean.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.BOOLEAN;

		} else if (int.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.INT;

		} else if (float.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.FLOAT;

		} else if (double.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.DOUBLE;

		} else if (short.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.SHORT;

		} else if (long.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.LONG;

		} else if (char.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.CHAR;

		} else if (byte.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return TypeName.BYTE;

		// map jaxrs response objects to retrofit ones
		} else if (javax.ws.rs.core.Response.class.getName().equals(jaxRsType.getFullyQualifiedName())) {
			return ClassName.get(Response.class);

		} else if (jaxRsType instanceof JavaParameterizedType) {
			JavaParameterizedType parametrizedType = (JavaParameterizedType) jaxRsType;
			if (parametrizedType.getActualTypeArguments().size() == 0) {
				return ClassName.bestGuess(jaxRsType.getFullyQualifiedName());
			}

			ClassName outerType = ClassName.bestGuess(parametrizedType.getFullyQualifiedName());
			TypeName[] paramTypes = new TypeName[parametrizedType.getActualTypeArguments().size()];
			for (int i = 0; i < paramTypes.length; ++i) {
				paramTypes[i] = ClassName.bestGuess(parametrizedType.getActualTypeArguments().get(i).getFullyQualifiedName());
			}
			return ParameterizedTypeName.get(outerType, paramTypes);

		} else {
			return ClassName.bestGuess(jaxRsType.getFullyQualifiedName());
		}
	}

}
