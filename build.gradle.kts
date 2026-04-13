plugins {
    // Root project currently does not apply additional plugins.
}

allprojects {
    group = "ovh.mythmc"
    version = providers.gradleProperty("version").get()

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://jitpack.io")
        }

        maven {
            url = uri("https://repo.codemc.io/repository/maven-releases/")
        }

        maven {
            url = uri("https://repo.mythmc.ovh/releases/")
        }

        maven {
            url = uri("https://repo.maven.apache.org/maven2/")
        }

        maven {
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }

        maven {
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }

        maven {
            url = uri("https://nexus.scarsz.me/content/groups/public/")
        }

        maven {
            url = uri("https://repo.papermc.io/repository/maven-snapshots/")
        }
    }
}

subprojects {
    plugins.withType<JavaPlugin> {
        the<JavaPluginExtension>().apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc>().configureEach {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).applyExternalLinks()
    }
}

tasks.register<Javadoc>("javadocMerged") {
    description = "Generates merged Javadoc for social-api and social-api-bukkit."

    val api = project(":social-api")
    val apiBukkit = project(":social-api-bukkit")

    val apiSourceSets = api.extensions.getByName("sourceSets") as SourceSetContainer
    val apiBukkitSourceSets = apiBukkit.extensions.getByName("sourceSets") as SourceSetContainer

    val apiMain = apiSourceSets.getByName("main")
    val apiBukkitMain = apiBukkitSourceSets.getByName("main")
    
    destinationDir = layout.buildDirectory.dir("docs/javadocMerged").get().asFile

    source = apiMain.allJava + apiBukkitMain.allJava
    classpath = apiMain.compileClasspath + apiBukkitMain.compileClasspath

    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).applyExternalLinks()
}

fun StandardJavadocDocletOptions.applyExternalLinks() {
    links("https://docs.oracle.com/en/java/javase/17/docs/api/")
    links("https://jd.advntr.dev/api/4.26.1/")
    links("https://javadoc.io/doc/org.jetbrains/annotations/26.1.0/")
    links("https://jd.papermc.io/paper/1.21.11/")
}