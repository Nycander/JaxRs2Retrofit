package de.bitdroid.jaxrs2retrofit.example.resources;


import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/helloWorld")
public interface MyResource {

	@GET
	public String getHelloWorld();

	@GET
	@Path("/{path}")
	public String getHelloWorld(
			@PathParam("path") String path,
			@QueryParam("stringQuery") String stringQuery,
			@QueryParam("booleanQuery") boolean booleanQuery,
			@QueryParam("intQuery") int intQuery,
			@QueryParam("doubleQuery") double doubleQuery,
			@QueryParam("floatQuery") float floatQuery,
			@QueryParam("shortQuery") short shortQuery,
			@QueryParam("longQuery") long longQuery,
			@QueryParam("byteQuery") byte byteQuery,
			@HeaderParam("header") String header);

	@POST
	public String postHello();

	@DELETE
	public String deleteHello();

	@PUT
	public String putHello();

}
