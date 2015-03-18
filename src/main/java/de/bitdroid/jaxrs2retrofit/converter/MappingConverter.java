package de.bitdroid.jaxrs2retrofit.converter;


import com.squareup.javapoet.ClassName;

/**
 * Simple converter for mapping annotation to a different class, e.g. the JaxRs {@link javax.ws.rs.HeaderParam}
 * to retrofits {@link retrofit.http.Header}.
 */
public final class MappingConverter implements ParamConverter {

	private final ClassName targetAnnotationType;

	public MappingConverter(ClassName targetAnnotationType) {
		this.targetAnnotationType = targetAnnotationType;
	}


	@Override
	public AnnotatedParam convert(AnnotatedParam param) {
		return new AnnotatedParam(
				param.getParamType(),
				targetAnnotationType,
				param.getAnnotationParameterMap());
	}

}
