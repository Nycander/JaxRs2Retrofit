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
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import de.bitdroid.jaxrs2retrofit.resources.SimpleResource;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import retrofit.RestAdapter;


@RunWith(JMockit.class)
public final class ResourceTest {

	private static final String
			HOST_ADDRESS = "http://localhost:12345/",
			OUTPUT_DIR = "testWorkingDir/",
			RESOURCES_DIR = System.getProperty("user.dir") + "/src/test/java/" + SimpleResource.class.getPackage().getName().replaceAll("\\.", "/"),
			CLIENT_PACKAGE = "client";

	@Mocked private SimpleResource resource;
	private Class<SimpleResource> resourceClass = SimpleResource.class;

	private HttpServer server;


	@Before
	public void startServer() throws Exception {
		ResourceConfig config = new ResourceConfig(resource.getClass());
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
		RetrofitGenerator generator = new RetrofitGenerator(RetrofitReturnStrategy.REGULAR, CLIENT_PACKAGE);
		JavaFile clientSource = generator.createResource(resource);

		// write client to file
		File clientFile = new File(OUTPUT_DIR);
		clientSource.writeTo(clientFile);
		clientSource.writeTo(System.out);

		// compile
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, new File(OUTPUT_DIR).getPath() + "/" + CLIENT_PACKAGE + "/SimpleResource.java");

		ClassLoader classLoader = new URLClassLoader(new URL[] { new URL("file://" + new File(OUTPUT_DIR).getAbsolutePath() + "/") });
		Class clientClass = classLoader.loadClass(CLIENT_PACKAGE + "." + resourceClass.getSimpleName());

		// setup retrofit client
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(HOST_ADDRESS)
				.build();

		Object client = adapter.create(clientClass);
		for (Method method : clientClass.getDeclaredMethods()) {
			String[] args = new String[method.getParameterTypes().length];
			for (int i = 0; i < args.length; ++i) args[i] = "foo";

			if (args.length == 0) method.invoke(client);
			else method.invoke(client, args);

			new Verifications() {{
				ResourceTest.this.resource.getHelloWorld(); times = 1;
			}};
		}
	}

}
