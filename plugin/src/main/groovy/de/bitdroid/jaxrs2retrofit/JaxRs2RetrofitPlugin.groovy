package de.bitdroid.jaxrs2retrofit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

public final class JaxRs2RetrofitPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def task = project.tasks.create('jaxRs2Retrofit', JaxRs2RetrofitTask)
        boolean hasJava = project.plugins.hasPlugin JavaPlugin
        if (hasJava) {
            project.compileJava.dependsOn task
            project.compileJava.source += task.outputs.files
        }
    }

}