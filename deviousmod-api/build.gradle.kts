plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.github.allinkdev"
version = project.property("api_version")!!
description = "Minecraft-independent Java 8+ API exposing silhouettes of DeviousMod classes"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val require: Configuration by configurations.creating

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
    maven("https://maven.lenni0451.net/releases/")
}

dependencies {
    compileOnlyApi("com.mojang:brigadier:${project.property("brigadier_version")}") // This dependency is marked as compile-only as it is expected to be provided in the environment of dependents.
    compileOnlyApi("io.github.spair:imgui-java-binding:${project.property("imgui_version")}")
    require("net.lenni0451:LambdaEvents:${project.property("lambdaevents_version")}")
}

configurations {
    api { extendsFrom(require) }
    implementation { extendsFrom(require) }
}

tasks {
    jar {
        dependsOn(require)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(require.map { zipTree(it) })
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        // ./gradlew --no-daemon publishMavenPublicationToReleasesRepository
        maven {
            name = "releases"
            url = uri("https://maven.allink.esixtwo.one/releases")
            credentials {
                username = System.getenv("allinkMavenUser")
                password = System.getenv("allinkMavenPassword")
            }
        }

        // ./gradlew --no-daemon publishMavenPublicationToSnapshotsRepository
        maven {
            name = "snapshots"
            url = uri("https://maven.allink.esixtwo.one/snapshots")
            credentials {
                username = System.getenv("allinkMavenUser")
                password = System.getenv("allinkMavenPassword")
            }
        }
    }
}