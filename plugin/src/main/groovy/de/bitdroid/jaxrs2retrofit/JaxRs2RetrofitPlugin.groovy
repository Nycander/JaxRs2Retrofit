package de.bitdroid.jaxrs2retrofit
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

public final class JaxRs2RetrofitPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        boolean hasJava = project.plugins.hasPlugin JavaPlugin
        boolean hasAndroidApp = project.plugins.hasPlugin AppPlugin
        boolean hasAndroidLib = project.plugins.hasPlugin LibraryPlugin

        if (hasJava) {
            def task = project.tasks.create('jaxRs2Retrofit', JaxRs2RetrofitTask)
            project.compileJava.dependsOn task
            project.compileJava.source += task.outputs.files
        }

        if (hasAndroidApp || hasAndroidLib) {
            def variants
            if (hasAndroidApp) variants = project.android.applicationVariants
            else variants = project.android.libraryVariants

            variants.all { variant ->
                def task = project.tasks.create("jaxRs2Retrofit-${variant.name}", JaxRs2RetrofitTask)
                variant.javaCompile.dependsOn task
                variant.registerJavaGeneratingTask task, task.outputDir
            }
        }

    }

}