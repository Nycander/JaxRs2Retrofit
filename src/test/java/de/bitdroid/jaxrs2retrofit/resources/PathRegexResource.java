package de.bitdroid.jaxrs2retrofit.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path("/path")
public interface PathRegexResource {

	@GET
	@Path("/{path}{regex:(/.*)?}")
	public String getRegex(@PathParam("path") String path, @PathParam("regex") String regex);

}
