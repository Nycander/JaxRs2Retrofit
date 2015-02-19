package de.bitdroid.jaxrs2retrofit;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.bitdroid.jaxrs2retrofit.resources.ResponseMappingResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


@RunWith(JMockit.class)
public final class ResponseMappingTest extends AbstractResourceTest<ResponseMappingResource> {

	@Mocked private ResponseMappingResource resource;

	public ResponseMappingTest() {
		super(ResponseMappingResource.class);
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testResponseMapping() throws Exception {
		doTestResponseMapping(client, clientClass.getDeclaredMethod("getSomething"), false);
		doTestResponseMapping(client, clientClass.getDeclaredMethod("getSomething", Callback.class), true);
		doTestResponseMapping(client, clientClass.getDeclaredMethod("deleteSomething"), false);
		doTestResponseMapping(client, clientClass.getDeclaredMethod("deleteSomething", Callback.class), true);
		new Verifications() {{
			resource.getSomething(); times = 2;
			resource.deleteSomething(); times = 2;
		}};
	}


	@Override
	protected ResponseMappingResource getMockedResource() {
		return resource;
	}


	private void doTestResponseMapping(Object client, Method method, boolean useCallback) throws Exception {
		MockCallbackHandler callbackHandler = new MockCallbackHandler();
		if (!useCallback) {
			try {
				method.invoke(client);
			} catch (InvocationTargetException ite) {
				RetrofitError re = (RetrofitError) ite.getTargetException();
				callbackHandler.failure(re);
			}
		} else {
			method.invoke(client, callbackHandler);
			Thread.sleep(100);
		}
	}


	private static final class MockCallbackHandler implements Callback<Response> {

		@Override
		public void success(Response response, Response response2) {  }

		@Override
		public void failure(RetrofitError error) {
			// since server is mocked it will return status code 0
			if (!error.getKind().equals(RetrofitError.Kind.UNEXPECTED) || !error.getCause().getMessage().contains("0")) {
				throw error;
			}
		}
	}

}
