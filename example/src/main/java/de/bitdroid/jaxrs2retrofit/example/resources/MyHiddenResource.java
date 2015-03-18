package de.bitdroid.jaxrs2retrofit.example.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/helloWorld")
public interface MyHiddenResource {

	@GET
	public String getHelloWorld();

}
