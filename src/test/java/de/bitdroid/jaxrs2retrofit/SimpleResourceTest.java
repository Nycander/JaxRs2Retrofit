package de.bitdroid.jaxrs2retrofit;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.SimpleResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import retrofit.RetrofitError;
import retrofit.client.Response;


@RunWith(JMockit.class)
public final class SimpleResourceTest extends AbstractResourceTest<SimpleResource> {

	@Mocked private SimpleResource resource;

	public SimpleResourceTest() {
		super(SimpleResource.class);
	}


	@Test
	public void doTestResource() throws Exception {
		for (Method method : clientClass.getDeclaredMethods()) {
			Object[] args = new Object[method.getParameterTypes().length];
			for (int i = 0; i < args.length; ++i) {
				args[i] = getArgument(method.getParameterTypes()[i]);
			}

			if (args.length == 0) method.invoke(client);
			else method.invoke(client, args);
		}

		new Verifications() {{
			resource.getHelloWorld(); times = 2;
			resource.getHelloWorld(
					anyString,
					anyString,
					anyBoolean,
					anyInt,
					anyDouble,
					anyFloat,
					anyShort,
					anyLong,
					anyByte,
					anyString ); times = 2;
		}};
	}


	@Override
	protected SimpleResource getMockedResource() {
		return resource;
	}


	private Object getArgument(Class<?> paramType) {
		if (String.class.equals(paramType)) {
			return "someString";

		} else if (int.class.equals(paramType)) {
			return 42;

		} else if (float.class.equals(paramType)) {
			return 42f;

		} else if (double.class.equals(paramType)) {
			return 42d;

		} else if (short.class.equals(paramType)) {
			return (short) 42;

		} else if (long.class.equals(paramType)) {
			return 42l;

		} else if (char.class.equals(paramType)) {
			return '*';

		} else if (byte.class.equals(paramType)) {
			return (byte) 42;

		} else if (boolean.class.equals(paramType)) {
			return true;

		} else if (retrofit.Callback.class.equals(paramType)) {
			return new retrofit.Callback() {
				@Override
				public void success(Object o, Response response) { }

				@Override
				public void failure(RetrofitError error) {
					throw error;
				}
			};
		} else {
			throw new IllegalArgumentException("no value found for type " + paramType.getName());
		}
	}

}
