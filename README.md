
[![Build Status](https://travis-ci.org/Maddoc42/JaxRs2Retrofit.svg?branch=master)](https://travis-ci.org/Maddoc42/JaxRs2Retrofit)
[ ![Download](https://api.bintray.com/packages/maddoc42/maven/jaxrs2retrofit/images/download.svg) ](https://bintray.com/maddoc42/maven/jaxrs2retrofit/_latestVersion)

JaxRs2Retrofit
==============

Creates [Retrofit](https://github.com/square/retrofit) classes based on
[JAX-RS](https://de.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services) interfaces. 

For example, given the following JAX-RS definition


```java
package serverPackage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/helloworld")
public interface SimpleResource {

	@GET
	@Path("/{path}")
	public String getHelloWorld(@PathParam("path") String path);

}
```

JaxRs2Retrofit will generate the Retrofit interface below

```java
package clientPackage;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface SimpleResource {

  @GET("/helloworld/{path}")
  String getHelloWorld(@Path("path") String path);
  
  @GET("/helloworld/{path}")
  void getHelloWorld(@Path("path") String path, Callback<String> callback);

}
```

Download & install
------------------

JaxRs2Retrofit can either be used as a gradle plugin, by directly using the generator classes or as a 
standalone jar.

The first two are available at available at [jcenter](https://bintray.com/maddoc42/maven/jaxrs2retrofit/).
For gradle based builds:

`'de.bitdroid.jaxrs2retrofit:jaxrs2retrofit:0.1.0'`

### Using the gradle plugin

In order to create a new gradle task that will generate Retrofit interfaces you must first include JaxRs2Retrofit
in the buildscript section

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'de.bitdroid.jaxrs2retrofit:jaxrs2retrofit:0.1.0'
    }
}
```

and create a new task

```
task jaxrs2retrofit(type: de.bitdroid.jaxrs2retrofit.JaxRs2RetrofitTask) {
    inputDir = new File("JAX RS SOURCE DIR")
    outputDir = new File(project.projectDir.toString() + "/target/generated-sources/jaxrs2retrofit/")
    retrofitPackageName = "RETROFIT.PACKAGE.NAME"
    excludedClassNamesRegex = "MyUnfinishedResource|MyHiddenResource"
}
project.compileJava.dependsOn jaxrs2retrofit
project.compileJava.source += jaxrs2retrofit.outputs.files
```

Notes about settings:
- `inputDir`: location of the JAX RS sources, e.g. `new File(project.projectDir.toString() + "/src/main/java")`
- `outputDir`: where the generated `.java` files should be stored
- `retrofitPackagename`: package name of generated files
- `excludedClassNamesRegex`: Java regex to exclude JAX RS files form generating Retrofit interfaces. Optional

### Using the standalone jar

Running the jar without any arguments will print a short help message. Options are similar to those of the gradle task.

Features
========

- Support for `GET`, `PUT`, `POST`, `DELETE` and `HEAD` http methods
- Converts `QueryParam`, `PathParam` and `HeaderParam` to their Retrofit counterpart
- Return values can be configured to use `retrofit.Callback`, behave normally or use both
- Skip classes / methods that lack JAX RS annotations
- Map `javax.ws.rs.core.Response` to `retrofit.client.Response`


Limitations
===========

- Resolving annotation values (e.g. `@Path(MyConstants.SOME_PATH)`) is very limited and works only with simple references
- `@Consume` and `@Produce` have no effect (include those in a `@Headers` section for each Retrofit method?)
- `@Context` annotated parameters are included in the Retrofit method parameter list
- JAX-RS path regex are ignored in Retrofit paths, e.g. `@Path("/{path}{regex:(/.*)?}")` 
  is translated to `@GET("/{path}/{regex}")`
