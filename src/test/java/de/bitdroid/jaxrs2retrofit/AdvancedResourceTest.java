package de.bitdroid.jaxrs2retrofit;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.AdvancedResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;


@RunWith(JMockit.class)
public final class AdvancedResourceTest extends AbstractResourceTest<AdvancedResource> {

	@Mocked private AdvancedResource resource;

	public AdvancedResourceTest() {
		super(AdvancedResource.class);
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testRegexElimination() throws Exception {
		Method regexMethod = clientClass.getDeclaredMethod("getRegex", String.class, String.class);
		regexMethod.invoke(client, "somePath", "someOtherPath");
		new Verifications() {{
			resource.getRegex(anyString, anyString); times = 1;
		}};
	}


	@Override
	protected AdvancedResource getMockedResource() {
		return resource;
	}

}
