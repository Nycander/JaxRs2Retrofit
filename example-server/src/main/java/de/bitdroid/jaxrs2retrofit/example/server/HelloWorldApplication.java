package de.bitdroid.jaxrs2retrofit.example.server;


import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfig> {

	public static void main(String[] args) throws Exception {
		new HelloWorldApplication().run(args);
	}

	@Override
	public void run(HelloWorldConfig configuration, Environment environment) throws Exception {
		environment.jersey().register(new MyHelloWorldResource());
	}

}
