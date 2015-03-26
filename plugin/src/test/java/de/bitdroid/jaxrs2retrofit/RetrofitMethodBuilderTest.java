package de.bitdroid.jaxrs2retrofit;


import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotNull;

import retrofit.Callback;
import rx.Observable;

public final class RetrofitMethodBuilderTest {

	private static final String METHOD_NAME = "testMethod";


	@Test
	public void testAddAnnotation() {
		AnnotationSpec annotation = AnnotationSpec.builder(ClassName.get(NotNull.class)).build();
		Collection<MethodSpec> methodSpecs = createBuilder(RetrofitReturnStrategy.REGULAR)
				.addAnnotation(annotation)
				.build().values();
		Assert.assertEquals(1, methodSpecs.size());
		MethodSpec method = methodSpecs.iterator().next();
		Assert.assertEquals(1, method.annotations.size());
		Assert.assertEquals(annotation, method.annotations.get(0));
	}


	@Test
	public void testAddParameter() {
		ParameterSpec parameter = ParameterSpec.builder(ClassName.get(String.class), "testParam").build();
				Collection < MethodSpec > methodSpecs = createBuilder(RetrofitReturnStrategy.REGULAR)
						.addParameter(parameter)
						.build().values();
		Assert.assertEquals(1, methodSpecs.size());
		MethodSpec method = methodSpecs.iterator().next();
		Assert.assertEquals(1, method.parameters.size());
		Assert.assertEquals(parameter, method.parameters.get(0));
	}


	@Test
	public void testRegularReturnStrategy() {
		Collection<MethodSpec> methodSpecs = createBuilder(RetrofitReturnStrategy.REGULAR).build().values();
		Assert.assertEquals(1, methodSpecs.size());
		testRegularReturnStrategy(methodSpecs.iterator().next());
	}


	@Test
	public void testObservableReturnStrategy() {
		Collection<MethodSpec> methodSpecs = createBuilder(RetrofitReturnStrategy.OBSERVABLE).build().values();
		Assert.assertEquals(1, methodSpecs.size());
		testObservableReturnStrategy(methodSpecs.iterator().next());
	}


	@Test
	public void testCallbackReturnStrategy() {
		Collection<MethodSpec> methodSpecs = createBuilder(RetrofitReturnStrategy.CALLBACK).build().values();
		Assert.assertEquals(1, methodSpecs.size());
		testCallbackReturnStrategy(methodSpecs.iterator().next());
	}


	@Test
	public void testAllReturnStrategy() {
		Map<RetrofitReturnStrategy, MethodSpec> methodSpecs = createBuilder(RetrofitReturnStrategy.ALL).build();
		Assert.assertEquals(3, methodSpecs.size());
		Assert.assertTrue(methodSpecs.containsKey(RetrofitReturnStrategy.REGULAR));
		Assert.assertTrue(methodSpecs.containsKey(RetrofitReturnStrategy.OBSERVABLE));
		Assert.assertTrue(methodSpecs.containsKey(RetrofitReturnStrategy.CALLBACK));
		for (Map.Entry<RetrofitReturnStrategy, MethodSpec> entry : methodSpecs.entrySet()) {
			switch (entry.getKey()) {
				case REGULAR:
					testRegularReturnStrategy(entry.getValue());
					break;

				case OBSERVABLE:
					testObservableReturnStrategy(entry.getValue());
					break;

				case CALLBACK:
					testCallbackReturnStrategy(entry.getValue());
					break;
			}
		}
	}


	private void testRegularReturnStrategy(MethodSpec spec) {
		Assert.assertEquals(ClassName.get(String.class), spec.returnType);
		Assert.assertEquals(0, spec.parameters.size());
		Assert.assertEquals(METHOD_NAME + "Synchronously", spec.name);
	}


	private void testObservableReturnStrategy(MethodSpec spec) {
		Assert.assertEquals(
				ParameterizedTypeName.get(ClassName.get(Observable.class), ClassName.get(String.class)),
				spec.returnType);
		Assert.assertEquals(0, spec.parameters.size());
		Assert.assertEquals(METHOD_NAME, spec.name);
	}


	private void testCallbackReturnStrategy(MethodSpec spec) {
		Assert.assertEquals(TypeName.VOID, spec.returnType);
		Assert.assertEquals(1, spec.parameters.size());
		Assert.assertEquals(
				ParameterizedTypeName.get(ClassName.get(Callback.class), ClassName.get(String.class)),
				spec.parameters.get(0).type);
		Assert.assertEquals(METHOD_NAME , spec.name);
	}


	private RetrofitMethodBuilder createBuilder(RetrofitReturnStrategy strategy) {
		return new RetrofitMethodBuilder(METHOD_NAME, strategy)
				.setReturnType(ClassName.get(String.class));
	}

}
