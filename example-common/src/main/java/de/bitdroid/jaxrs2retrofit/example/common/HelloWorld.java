package de.bitdroid.jaxrs2retrofit.example.common;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloWorld {

	private final String hello, world;

	@JsonCreator
	public HelloWorld(
			@JsonProperty("hello") String hello,
			@JsonProperty("world") String world) {

		this.hello = hello;
		this.world = world;
	}

	public String getHello() {
		return hello;
	}

	public String getWorld() {
		return world;
	}

}
