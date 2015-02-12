package de.bitdroid.jaxrs2retrofit.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/somepath")
public interface SimpleResource {

	@GET
	public String getHelloWorld();

}
