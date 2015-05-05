package de.bitdroid.jaxrs2retrofit
import com.squareup.javapoet.JavaFile
import com.thoughtworks.qdox.JavaProjectBuilder
import com.thoughtworks.qdox.model.JavaClass
import de.bitdroid.jaxrs2retrofit.converter.ParamConverterManager
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

public class JaxRs2RetrofitTask extends DefaultTask {

    @InputDirectory File inputDir = new File("${project.projectDir}/src/main/java")
    @OutputDirectory File outputDir = new File("${project.projectDir}/build/generated/source/jaxrs2retrofit")
    String packageName = 'de.bitdroid.jaxrs2retrofit'
    String excludedClassNamesRegex = ''
    boolean generateSynchronousMethods = true;
    boolean generateCallbackMethods = true;
    boolean generateRxJavaMethods = true;
    ParamConverterManager paramConverterManager = ParamConverterManager.getDefaultInstance();

    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {
        RetrofitGenerator generator = new RetrofitGenerator(
                new GeneratorSettings(
                        packageName,
                        excludedClassNamesRegex,
                        generateSynchronousMethods,
                        generateCallbackMethods,
                        generateRxJavaMethods,
                        paramConverterManager));
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(inputDir);
        for (JavaClass javaClass : builder.getClasses()) {
            JavaFile javaFile = generator.createResource(javaClass);
            if (javaFile == null) continue;
            javaFile.writeTo(outputDir);
        }
    }


    @Override
    public String getDescription() {
        return 'Creates Retrofit interfaces base on JAX RS resources.';
    }

 }
