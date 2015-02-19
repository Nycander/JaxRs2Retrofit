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


@RunWith(JMockit.class)
public abstract class AbstractResourceTest<T> {

	private static final String
			HOST_ADDRESS = "http://localhost:12345/",
			OUTPUT_DIR = "testWorkingDir/",
			RESOURCES_DIR = System.getProperty("user.dir") + "/src/test/java/" + SimpleResource.class.getPackage().getName().replaceAll("\\.", "/"),
			CLIENT_PACKAGE = "client";

	private final Class<T> resourceClass;
	private HttpServer server;

	protected Object client;
	protected Class clientClass;

	protected AbstractResourceTest(Class<T> resourceClass) {
		this.resourceClass = resourceClass;
	}


	@Before
	public void startServer() throws Exception {
		ResourceConfig config = new ResourceConfig(getMockedResource().getClass());
		server = JdkHttpServerFactory.createHttpServer(new URI(HOST_ADDRESS), config, true);
	}


	@Before
	public void createClientClassAndObject() throws Exception {
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
		Assert.assertTrue(clientFile.mkdir());
		clientSource.writeTo(clientFile);
		clientSource.writeTo(System.out);

		// compile
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, new File(OUTPUT_DIR).getPath() + "/" + CLIENT_PACKAGE + "/" + resourceClass.getSimpleName() + ".java");

		ClassLoader classLoader = new URLClassLoader(new URL[] { new URL("file://" + new File(OUTPUT_DIR).getAbsolutePath() + "/") });
		this.clientClass = classLoader.loadClass(CLIENT_PACKAGE + "." + resourceClass.getSimpleName());

		// setup retrofit client
		RestAdapter adapter = getRestAdapterBuilder().build();

		this.client = adapter.create(clientClass);
		Assert.assertEquals(2 * resourceClass.getDeclaredMethods().length, clientClass.getDeclaredMethods().length);
	}


	@After
	public void stopServer() {
		server.stop(0);
	}


	@After
	public void removeGeneratedFiles() throws Exception {
		FileUtils.deleteDirectory(new File(OUTPUT_DIR));
	}


	protected abstract T getMockedResource();


	protected RestAdapter.Builder getRestAdapterBuilder() {
		return new RestAdapter.Builder().setEndpoint(HOST_ADDRESS);
	}

}
