package de.bitdroid.jaxrs2retrofit.example;

import retrofit.RestAdapter;

public class Main {

	public static void main(String[] args) {
		MyResource myResource = new RestAdapter.Builder()
				.setEndpoint("http://example.com")
				.build()
				.create(MyResource.class);

		String helloWorld = myResource.getHelloWorldSynchronously();
		System.out.println(helloWorld);
	}

}
