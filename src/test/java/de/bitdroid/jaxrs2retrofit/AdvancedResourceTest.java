package de.bitdroid.jaxrs2retrofit;


import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.AdvancedResource;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import retrofit.RetrofitError;


@RunWith(JMockit.class)
public final class AdvancedResourceTest extends AbstractResourceTest<AdvancedResource> {

	@Mocked private AdvancedResource resource;

	public AdvancedResourceTest() {
		super(AdvancedResource.class);
	}


	@Override
	@SuppressWarnings("unchecked")
	protected void doTestResource(Object client, Class clientClass) throws Exception {
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
	}


	@Override
	protected AdvancedResource getMockedResource() {
		return resource;
	}

}
