package de.bitdroid.jaxrs2retrofit.example;

import de.bitdroid.jaxrs2retrofit.example.common.HelloWorld;
import retrofit.RestAdapter;

public class Main {

	public static void main(String[] args) {
		MyHelloWorldResource myResource = new RestAdapter.Builder()
				.setEndpoint("http://localhost:8080")
				.build()
				.create(MyHelloWorldResource.class);

		HelloWorld helloWorld = myResource.getHelloWorldSynchronously();
		System.out.print(helloWorld.getHello() + " " + helloWorld.getWorld());
	}

}
