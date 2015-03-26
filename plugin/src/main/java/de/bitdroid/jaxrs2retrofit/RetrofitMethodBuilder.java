package de.bitdroid.jaxrs2retrofit;


import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import retrofit.Callback;
import rx.Observable;

public final class RetrofitMethodBuilder {

	private static final String SYNCHRONOUS_METHODS_PREFIX = "Synchronously";

	private final RetrofitReturnStrategy returnType;
	private final Map<RetrofitReturnStrategy, MethodSpec.Builder> methodBuilders = new HashMap<>();

	public RetrofitMethodBuilder(String methodName, RetrofitReturnStrategy returnType) {
		this.returnType = returnType;
		if (isMatchingReturnType(RetrofitReturnStrategy.REGULAR)) {
			methodBuilders.put(
					RetrofitReturnStrategy.REGULAR,
					applyModifiers(MethodSpec.methodBuilder(methodName + SYNCHRONOUS_METHODS_PREFIX)));
		}
		if (isMatchingReturnType(RetrofitReturnStrategy.CALLBACK)) {
			methodBuilders.put(
					RetrofitReturnStrategy.CALLBACK,
					applyModifiers(MethodSpec.methodBuilder(methodName)));
		}
		if (isMatchingReturnType(RetrofitReturnStrategy.OBSERVABLE)) {
			methodBuilders.put(
					RetrofitReturnStrategy.OBSERVABLE,
					applyModifiers(MethodSpec.methodBuilder(methodName)));
		}
	}


	public RetrofitMethodBuilder addAnnotation(AnnotationSpec annotation) {
		for (MethodSpec.Builder builder : methodBuilders.values()) builder.addAnnotation(annotation);
		return this;
	}


	public RetrofitMethodBuilder addParameter(ParameterSpec parameter) {
		for (MethodSpec.Builder builder : methodBuilders.values()) builder.addParameter(parameter);
		return this;
	}


	public RetrofitMethodBuilder setReturnType(TypeName returnTypeName) {
		for (Map.Entry<RetrofitReturnStrategy, MethodSpec.Builder> entry : methodBuilders.entrySet()) {
			MethodSpec.Builder builder = entry.getValue();
			switch (entry.getKey()) {
				case REGULAR:
					builder.returns(returnTypeName);
					break;

				case CALLBACK:
					ParameterSpec callback = ParameterSpec
							.builder(ParameterizedTypeName.get(ClassName.get(Callback.class), returnTypeName), "callback")
							.build();
					builder.addParameter(callback);
					builder.returns(TypeName.VOID);
					break;

				case OBSERVABLE:
					TypeName observable = ParameterizedTypeName.get(ClassName.get(Observable.class), returnTypeName);
					builder.returns(observable);
					break;
			}
		}
		return this;
	}


	public List<MethodSpec> build() {
		List<MethodSpec> result = new LinkedList<>();
		for (MethodSpec.Builder builder : methodBuilders.values()) result.add(builder.build());
		return result;
	}


	private MethodSpec.Builder applyModifiers(MethodSpec.Builder builder) {
		return builder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
	}


	private boolean isMatchingReturnType(RetrofitReturnStrategy targetReturnType) {
		return returnType.equals(targetReturnType) || returnType.equals(RetrofitReturnStrategy.ALL);
	}

}
