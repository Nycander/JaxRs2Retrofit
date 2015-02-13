package de.bitdroid.jaxrs2retrofit;


import org.junit.runner.RunWith;

import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.SimpleResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;


@RunWith(JMockit.class)
public final class SimpleResourceTest extends AbstractResourceTest<SimpleResource> {

	@Mocked private SimpleResource resource;

	public SimpleResourceTest() {
		super(SimpleResource.class);
	}


	@Override
	protected void doTestResource(Object client, Class clientClass) throws Exception {
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

}
