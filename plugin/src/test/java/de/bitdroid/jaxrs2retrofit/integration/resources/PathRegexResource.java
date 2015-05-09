package de.bitdroid.jaxrs2retrofit.integration.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path("/path")
public interface PathRegexResource {

	@GET
	@Path("/{path}{regex:(/.*)?}")
	public String getRegex(@PathParam("path") String path, @PathParam("regex") String regex);

	@GET
	@Path("/path-2")
	public String getRegular();

}
