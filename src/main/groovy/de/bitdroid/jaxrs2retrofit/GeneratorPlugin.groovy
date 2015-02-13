package de.bitdroid.jaxrs2retrofit
import org.gradle.api.Plugin
import org.gradle.api.Project

class GeneratorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def task = project.tasks.create("jaxrs2retrofit", GeneratorTask)
        task.outputDir = new File("${project.buildDir}/generated/source/jaxrs2retrofit")
        project.compileJava.dependsOn task
        project.compileJava.source += task.outputs.files
    }

}
