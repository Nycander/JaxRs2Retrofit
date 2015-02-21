package de.bitdroid.jaxrs2retrofit;


import org.junit.Test;
import org.junit.runner.RunWith;

import de.bitdroid.jaxrs2retrofit.resources.ContentTypeResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;


@RunWith(JMockit.class)
public final class ContentTypeTest extends AbstractResourceTest<ContentTypeResource> {

	@Mocked private ContentTypeResource resource;

	public ContentTypeTest() {
		super(ContentTypeResource.class);
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testResponseMapping() throws Exception {
		// simply run methods as Jersey will return 415 if content type is incorrect.
		clientClass.getDeclaredMethod("getJson", String.class).invoke(client, "some json");
		clientClass.getDeclaredMethod("getXml", String.class).invoke(client, "some xml");

		new Verifications() {{
			resource.getJson(anyString); times = 1;
			resource.getXml(anyString); times = 1;
		}};
	}


	@Override
	protected ContentTypeResource getMockedResource() {
		return resource;
	}

}
