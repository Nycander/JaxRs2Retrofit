package de.bitdroid.jaxrs2retrofit.resources;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("/path")
@Consumes(MediaType.APPLICATION_JSON)
public interface CustomAnnotationResource {

	/**
	 * Parameter should be ignored completely.
	 */
	@GET
	@Path("/value1")
	public String dropParameter(@CustomAnnotation String value);

	/**
	 * Parameter should be converter to a different type.
	 */
	@GET
	@Path("/value2")
	public String convertAnnotation(@CustomAnnotation boolean value);

	/**
	 * Annotation should be ignored but not the parameter.
	 */
	@GET
	@Path("/value3")
	public String dropAnnotation(@NotNull @Valid String value);

}
