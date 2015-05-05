package de.bitdroid.jaxrs2retrofit.example.server;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.bitdroid.jaxrs2retrofit.example.common.HelloWorld;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MyHelloWorldResource {

	private HelloWorld helloWorld = new HelloWorld("hello", "world");

	@GET
	public HelloWorld getHelloWorld() {
		if (helloWorld == null) throw new NotFoundException("no hello world here ...");
		return helloWorld;
	}


	@PUT
	public void setHelloWorld(HelloWorld helloWorld) {
		this.helloWorld = helloWorld;
	}


	@DELETE
	public void deleteHelloWorld() {
		this.helloWorld = null;
	}

}
