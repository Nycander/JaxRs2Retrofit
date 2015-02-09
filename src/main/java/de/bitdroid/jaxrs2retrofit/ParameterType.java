package de.bitdroid.jaxrs2retrofit;


import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import retrofit.http.Body;
import retrofit.http.Path;
import retrofit.http.Query;

public enum ParameterType {

	PATH(PathParam.class, Path.class),
	QUERY(QueryParam.class, Query.class),
	BODY(null, Body.class);


	private final Class<?> jaxRsClass;
	private final Class<?> retrofitClass;


	ParameterType(Class<?> jaxRsClass, Class<?> retrofitClass) {
		this.jaxRsClass = jaxRsClass;
		this.retrofitClass = retrofitClass;
	}

	public Class<?> getJaxRsClass() {
		return jaxRsClass;
	}

	public Class<?> getRetrofitClass() {
		return retrofitClass;
	}

	private static final Map<String, ParameterType> jaxRsClassNameToParam = new HashMap<>();
	static {
		for (ParameterType type : values()) {
			if (type.getJaxRsClass() != null) {
				jaxRsClassNameToParam.put(type.getJaxRsClass().getName(), type);
			}
		}
	}

	public static ParameterType forJaxRsClassName(String jaxRsClassName) {
		return jaxRsClassNameToParam.get(jaxRsClassName);
	}
}
