/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    compileOnly(project(":social-api"))
    implementation(project(path = ":social-bukkit", configuration = "shadow"))
    compileOnly(libs.org.incendo.cloud.paper)
    compileOnly(libs.io.papermc.paper.paper.api)
}

tasks {
    processResources {
        val replacements = mapOf(
            "version" to version.toString()
        )
        inputs.properties(replacements)
        filesMatching("paper-plugin.yml") {
            expand(replacements)
        }
    }
}

description = "social-paper"
java.sourceCompatibility = JavaVersion.VERSION_21