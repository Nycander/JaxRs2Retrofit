package de.bitdroid.jaxrs2retrofit;


public enum RetrofitReturnStrategy {

	REGULAR, 	// regular Java return types
	CALLBACK,	// Retrofit callbacks with 'void' return type
	OBSERVABLE	// Rx Observables

}
