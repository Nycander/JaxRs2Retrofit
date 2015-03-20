package de.bitdroid.jaxrs2retrofit.converter;


/**
 * Simple converter for ignoring parameters completely (java type + annotation).
 */
public final class IgnoreConverter implements ParamConverter {

	@Override
	public AnnotatedParam convert(AnnotatedParam param) {
		return null;
	}

}
