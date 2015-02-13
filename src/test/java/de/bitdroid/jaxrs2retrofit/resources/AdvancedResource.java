package de.bitdroid.jaxrs2retrofit.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("/path")
public interface AdvancedResource {

	@GET
	public Response getResponse();

}
