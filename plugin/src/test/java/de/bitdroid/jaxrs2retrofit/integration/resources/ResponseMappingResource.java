package de.bitdroid.jaxrs2retrofit.integration.resources;


import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("/path")
public interface ResponseMappingResource {

	@GET
	public Response getSomething();

	@DELETE
	public Response deleteSomething();

}
