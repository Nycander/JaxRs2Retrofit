package de.bitdroid.jaxrs2retrofit.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("/path")
@Consumes(MediaType.APPLICATION_JSON)
public interface CustomAnnotationResource extends SimpleResource {

	@GET
	@Path("/value1")
	public String dropAnnotation(@CustomAnnotation String value);

	@GET
	@Path("/value2")
	public String convertAnnotation(@CustomAnnotation boolean value);

}
