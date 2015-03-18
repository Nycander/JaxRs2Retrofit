package de.bitdroid.jaxrs2retrofit.converter;


import com.google.common.base.Objects;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Map;


/**
 * Parameter including one annotation and its annotation parameters.
 */
public class AnnotatedParam {

	private final TypeName paramType;
	private final ClassName annotationType;
	private final Map<String, Object> annotationParameterMap;

	public AnnotatedParam(
			TypeName paramType,
			ClassName annotationType,
			Map<String, Object> annotationParameterMap) {

		this.paramType = paramType;
		this.annotationType = annotationType;
		this.annotationParameterMap = annotationParameterMap;
	}


	public TypeName getParamType() {
		return paramType;
	}


	public ClassName getAnnotationType() {
		return annotationType;
	}


	public Map<String, Object> getAnnotationParameterMap() {
		return annotationParameterMap;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof AnnotatedParam)) return false;
		AnnotatedParam param = (AnnotatedParam) other;
		return Objects.equal(paramType, param.paramType)
				&& Objects.equal(annotationType, param.annotationType)
				&& Objects.equal(annotationParameterMap, param.annotationParameterMap);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(paramType, annotationType, annotationParameterMap);
	}

}
