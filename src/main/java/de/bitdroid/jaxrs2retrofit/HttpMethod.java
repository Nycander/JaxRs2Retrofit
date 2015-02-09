package de.bitdroid.jaxrs2retrofit;


import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {

	GET(javax.ws.rs.GET.class, retrofit.http.GET.class),
	HEAD(javax.ws.rs.HEAD.class, retrofit.http.HEAD.class),
	POST(javax.ws.rs.POST.class, retrofit.http.POST.class),
	PUT(javax.ws.rs.PUT.class, retrofit.http.PUT.class),
	DELETE(javax.ws.rs.DELETE.class, retrofit.http.DELETE.class);

	private final Class<?> jaxRsClass;
	private final Class<?> retrofitClass;

	HttpMethod(Class<?> jaxRsClass, Class<?> retrofitClass) {
		this.jaxRsClass = jaxRsClass;
		this.retrofitClass = retrofitClass;
	}

	public Class<?> getJaxRsClass() {
		return jaxRsClass;
	}

	public Class<?> getRetrofitClass() {
		return retrofitClass;
	}

	private static final Map<String, HttpMethod> jaxRsClassNameToMethod = new HashMap<>();
	static {
		for (HttpMethod method : values()) {
			jaxRsClassNameToMethod.put(method.getJaxRsClass().getName(), method);
		}
	}

	public static HttpMethod forJaxRsClassName(String jaxRsClassName) {
		return jaxRsClassNameToMethod.get(jaxRsClassName);
	}

}
