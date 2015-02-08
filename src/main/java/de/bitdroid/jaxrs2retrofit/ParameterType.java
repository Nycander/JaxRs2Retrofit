package de.bitdroid.jaxrs2retrofit;


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

}
