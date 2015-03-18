package de.bitdroid.jaxrs2retrofit;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.bitdroid.jaxrs2retrofit.converter.AnnotatedParam;
import de.bitdroid.jaxrs2retrofit.converter.ParamConverter;
import de.bitdroid.jaxrs2retrofit.converter.ParamConverterManager;
import de.bitdroid.jaxrs2retrofit.resources.CustomAnnotation;
import de.bitdroid.jaxrs2retrofit.resources.CustomAnnotationResource;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import retrofit.http.Header;


/**
 * Tests that the {@link de.bitdroid.jaxrs2retrofit.resources.CustomAnnotation} is correctly converted
 * to some other value.
 */
@RunWith(JMockit.class)
public final class CustomAnnotationTest extends AbstractResourceTest<CustomAnnotationResource> {

	@Mocked private CustomAnnotationResource resource;

	public CustomAnnotationTest() {
		super(CustomAnnotationResource.class);
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testDropParam() throws Exception {
		clientClass.getDeclaredMethod("dropAnnotation");
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testParamToHeaderConversion() throws Exception {
		Method headerMethod = clientClass.getDeclaredMethod("convertAnnotation", String.class);
		Annotation[][] annotations = headerMethod.getParameterAnnotations();

		Assert.assertEquals(1, annotations.length);
		Assert.assertEquals(1, annotations[0].length);
		Assert.assertEquals(Header.class, annotations[0][0].annotationType());
	}


	@Override
	protected CustomAnnotationResource getMockedResource() {
		return resource;
	}


	@Override
	protected ParamConverterManager getParamConverterManager() {
		ParamConverterManager manager = super.getParamConverterManager();
		manager.registerConverter(
				ClassName.get(CustomAnnotation.class),
				new ParamConverter() {
					@Override
					public AnnotatedParam convert(AnnotatedParam param) {
						if (TypeName.BOOLEAN.equals(param.getParamType())) {
							Map<String, Object> args = new HashMap<>();
							args.put("value", "\"myHeader\"");
							return new AnnotatedParam(
									ClassName.get(String.class),
									ClassName.get(Header.class),
									args);
						} else {
							return null;
						}
					}
				});
		return manager;
	}


	@Override
	protected ResourceConfig getResourceConfig() {
		ResourceConfig config = super.getResourceConfig();
		config.register(new CustomAnnotationBinder());
		return config;
	}


	public static final class CustomAnnotationResolver extends ParamInjectionResolver<CustomAnnotation> {

		public CustomAnnotationResolver() {
			super(CustomAnnotationProvider.class);
		}
	}


	public static final class CustomAnnotationProvider extends AbstractValueFactoryProvider {

		@Inject
		protected CustomAnnotationProvider(
				MultivaluedParameterExtractorProvider mpep,
				ServiceLocator locator) {

			super(mpep, locator, Parameter.Source.UNKNOWN);
		}


		@Override
		protected Factory<String> createValueFactory(Parameter parameter) {
			return new Factory<String>() {
				@Override
				public String provide() {
					return "hello world";
				}

				@Override
				public void dispose(String instance) {  }
			};
		}

	}


	public static final class CustomAnnotationBinder extends AbstractBinder {

		@Override
		protected void configure() {
			bind(CustomAnnotationProvider.class)
					.to(ValueFactoryProvider.class)
					.in(Singleton.class);

			bind(CustomAnnotationResolver.class)
					.to(new TypeLiteral<InjectionResolver<CustomAnnotation>>() {
					})
					.in(Singleton.class);
		}

	}

}
