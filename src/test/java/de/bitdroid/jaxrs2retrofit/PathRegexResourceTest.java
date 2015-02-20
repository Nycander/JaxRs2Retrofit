package de.bitdroid.jaxrs2retrofit;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.PathRegexResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;


@RunWith(JMockit.class)
public final class PathRegexResourceTest extends AbstractResourceTest<PathRegexResource> {

	@Mocked private PathRegexResource resource;

	public PathRegexResourceTest() {
		super(PathRegexResource.class);
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testRegexElimination() throws Exception {
		Method regexMethod = clientClass.getDeclaredMethod("getRegex", String.class, String.class);
		regexMethod.invoke(client, "somePath", "someOtherPath");
		clientClass.getDeclaredMethod("getRegular").invoke(client);

		new Verifications() {{
			resource.getRegex(anyString, anyString); times = 1;
			resource.getRegular(); times = 1;
		}};
	}


	@Override
	protected PathRegexResource getMockedResource() {
		return resource;
	}

}
