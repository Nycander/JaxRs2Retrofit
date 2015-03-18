package de.bitdroid.jaxrs2retrofit.converter;


import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Stores instances of {@link de.bitdroid.jaxrs2retrofit.converter.ParamConverter} in memory.
 */
public final class ParamConverterManager {

	/**
	 * Returns an instance of this manager which is preconfigured with a number of converters,
	 * e.g. {@link javax.ws.rs.PathParam} to {@link retrofit.http.Path}.
	 */
	public static ParamConverterManager getDefaultInstance() {
		ParamConverterManager manager = new ParamConverterManager();
		manager.registerConverter(
				ClassName.get(HeaderParam.class),
				new MappingConverter(ClassName.get(Header.class)));
		manager.registerConverter(
				ClassName.get(PathParam.class),
				new MappingConverter(ClassName.get(Path.class)));
		manager.registerConverter(
				ClassName.get(QueryParam.class),
				new MappingConverter(ClassName.get(Query.class)));
		manager.registerConverter(
				ClassName.get(Void.class),
				new MappingConverter(ClassName.get(Body.class)));
		return manager;
	}


	private final Map<ClassName, ParamConverter> converterMap = new HashMap<>();


	public void registerConverter(ClassName annotationType, ParamConverter converter) {
		converterMap.put(annotationType, converter);
	}


	public ParamConverter getConverter(ClassName annotationType) {
		return converterMap.get(annotationType);
	}


	public boolean hasConverter(ClassName annotationType) {
		return converterMap.containsKey(annotationType);
	}

}
