package de.bitdroid.jaxrs2retrofit.resources;


import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/helloworld")
public interface SimpleResource {

	@GET
	public String getHelloWorld();

	@GET
	@Path("/{somePath}")
	public String getHelloWorld(
			@PathParam("somePath") String somePath,
			@QueryParam("someQuery") String someQuery,
			@HeaderParam("someHeader") String someHeader);

	@POST
	public String postHello();

	@DELETE
	public String deleteHello();

	@PUT
	public String putHello();

}
