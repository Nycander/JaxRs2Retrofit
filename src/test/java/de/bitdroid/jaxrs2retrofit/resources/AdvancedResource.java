package de.bitdroid.jaxrs2retrofit.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


@Path("/path")
public interface AdvancedResource {

	@GET
	public Response getResponse();


	@GET
	@Path("/{path}{regex:(/.*)?}")
	public String getRegex(@PathParam("path") String path, @PathParam("regex") String regex);

}
