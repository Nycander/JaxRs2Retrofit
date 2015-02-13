package de.bitdroid.jaxrs2retrofit;


import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.AdvancedResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import retrofit.RetrofitError;


@RunWith(JMockit.class)
public final class AdvancedResourceTest extends AbstractResourceTest<AdvancedResource> {

	@Mocked private AdvancedResource resource;

	public AdvancedResourceTest() {
		super(AdvancedResource.class);
	}


	@Override
	protected void doTestResource(Object client, Class clientClass) throws Exception {
		testResponseMapping(client, clientClass);
		testRegexElimination(client, clientClass);
	}


	@SuppressWarnings("unchecked")
	private void testResponseMapping(Object client, Class clientClass) throws Exception {
		Method getResourceMethod = clientClass.getDeclaredMethod("getResponse");
		try {
			getResourceMethod.invoke(client);
		} catch (InvocationTargetException ite) {
			// since server is mocked it will return status code 0
			RetrofitError re = (RetrofitError) ite.getTargetException();
			if (!re.getKind().equals(RetrofitError.Kind.UNEXPECTED) || !re.getCause().getMessage().contains("0")) {
				throw re;
			}
		}
		new Verifications() {{
			resource.getResponse(); times = 1;
		}};
	}


	@SuppressWarnings("unchecked")
	private void testRegexElimination(Object client, Class clientClass) throws Exception {
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
