package de.bitdroid.jaxrs2retrofit;


import de.bitdroid.jaxrs2retrofit.converter.ParamConverterManager;

/**
 * Collection of configurable options for the Retrofit generator.
 */
public class GeneratorSettings {

	private final String packageName;
	private final String excludedClassNamesRegex;
	private final ParamConverterManager paramConverterManager;
	private final boolean generateSynchronousMethods, generateCallbackMethods, generateRxJavaMethods;

	/**
	 * @param packageName package name of the generated classes
	 * @param excludedClassNamesRegex regex for excluding JaxRs resources
	 * @param paramConverterManager manager for all JaxRs param conversions
	 * @param generateSynchronousMethods whether blocking Retrofit methods should be generated
	 * @param generateCallbackMethods whether Retrofit methods using {@link retrofit.Callback} should be generated.
	 * @param generateRxJavaMethods whether Retrofit methods returning {@link rx.Observable} should be generated.
	 */
	public GeneratorSettings(
			String packageName,
			String excludedClassNamesRegex,
			boolean generateSynchronousMethods,
			boolean generateCallbackMethods,
			boolean generateRxJavaMethods,
			ParamConverterManager paramConverterManager) {

		this.packageName = packageName;
		this.excludedClassNamesRegex = excludedClassNamesRegex;
		this.generateSynchronousMethods = generateSynchronousMethods;
		this.generateCallbackMethods = generateCallbackMethods;
		this.generateRxJavaMethods = generateRxJavaMethods;
		this.paramConverterManager = paramConverterManager;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getExcludedClassNamesRegex() {
		return excludedClassNamesRegex;
	}

	public boolean getGenerateSynchronousMethods() {
		return generateSynchronousMethods;
	}

	public boolean getGenerateCallbackMethods() {
		return generateCallbackMethods;
	}

	public boolean getGenerateRxJavaMethods() {
		return generateRxJavaMethods;
	}

	public ParamConverterManager getParamConverterManager() {
		return paramConverterManager;
	}

}
