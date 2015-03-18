package de.bitdroid.jaxrs2retrofit.converter;


/**
 * Converts an annotated param into another one or returns null
 * in case the parameter should be ignored during code creation.
 */
public interface ParamConverter {

	public AnnotatedParam convert(AnnotatedParam param);

}
