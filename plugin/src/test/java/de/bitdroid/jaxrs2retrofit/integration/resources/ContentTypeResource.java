package de.bitdroid.jaxrs2retrofit.integration.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("/path2")
@Consumes(MediaType.APPLICATION_JSON)
public interface ContentTypeResource extends SimpleResource {

	@POST
	@Path("/json")
	public String getJson(String json);

	@POST
	@Path("/xml")
	@Consumes(MediaType.APPLICATION_XML)
	public String getXml(String xml);


	@POST
	@Path("/pdf")
	@Consumes("application/pdf")
	public String getPdf(String pdf);

}
