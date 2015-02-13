package de.bitdroid.jaxrs2retrofit;


import com.squareup.javapoet.JavaFile;
import com.sun.net.httpserver.HttpServer;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import de.bitdroid.jaxrs2retrofit.resources.SimpleResource;
import mockit.integration.junit4.JMockit;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


@RunWith(JMockit.class)
public abstract class AbstractResourceTest<T> {

	private static final String
			HOST_ADDRESS = "http://localhost:12345/",
			OUTPUT_DIR = "testWorkingDir/",
			RESOURCES_DIR = System.getProperty("user.dir") + "/src/test/java/" + SimpleResource.class.getPackage().getName().replaceAll("\\.", "/"),
			CLIENT_PACKAGE = "client";

	private final Class<T> resourceClass;
	private HttpServer server;

	protected AbstractResourceTest(Class<T> resourceClass) {
		this.resourceClass = resourceClass;
	}


	@Before
	public void startServer() throws Exception {
		ResourceConfig config = new ResourceConfig(getMockedResource().getClass());
		server = JdkHttpServerFactory.createHttpServer(new URI(HOST_ADDRESS), config, true);
	}


	@Before
	public void setupOutputDir() {
		Assert.assertTrue(new File(OUTPUT_DIR).mkdir());
	}


	@After
	public void stopServer() {
		server.stop(0);
	}


	@After
	public void removeOutputDir() throws Exception {
		FileUtils.deleteDirectory(new File(OUTPUT_DIR));
	}


	@Test
	public void testResource() throws Exception {
		// read resource Java files
		JavaProjectBuilder builder = new JavaProjectBuilder();
		File resourceDir = new File(RESOURCES_DIR);
		builder.addSourceTree(resourceDir);
		JavaClass resource = builder.getClassByName(resourceClass.getName());

		// generate retrofit client
		RetrofitGenerator generator = new RetrofitGenerator(RetrofitReturnStrategy.BOTH, CLIENT_PACKAGE, "");
		JavaFile clientSource = generator.createResource(resource);

		// write client to file
		File clientFile = new File(OUTPUT_DIR);
		clientSource.writeTo(clientFile);
		clientSource.writeTo(System.out);

		// compile
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, new File(OUTPUT_DIR).getPath() + "/" + CLIENT_PACKAGE + "/" + resourceClass.getSimpleName() + ".java");

		ClassLoader classLoader = new URLClassLoader(new URL[] { new URL("file://" + new File(OUTPUT_DIR).getAbsolutePath() + "/") });
		Class clientClass = classLoader.loadClass(CLIENT_PACKAGE + "." + resourceClass.getSimpleName());

		// setup retrofit client
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(HOST_ADDRESS)
				.build();

		Object client = adapter.create(clientClass);

		Assert.assertEquals(2 * resourceClass.getDeclaredMethods().length, clientClass.getDeclaredMethods().length);
		doTestResource(client, clientClass);
	}


	protected Object getArgument(Class<?> paramType) {
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


	protected abstract void doTestResource(Object client, Class clientClass) throws Exception;
	protected abstract T getMockedResource();

}
