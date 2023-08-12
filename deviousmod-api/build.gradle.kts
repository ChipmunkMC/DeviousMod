plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.github.allinkdev"
version = "1.3.0-SNAPSHOT"
description = "Minecraft-independent Java 8+ API exposing silhouettes of DeviousMod classes"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val shade: Configuration by configurations.creating

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
    shade("net.lenni0451:LambdaEvents:${project.property("lambdaevents_version")}")
}

configurations {
    api { extendsFrom(shade) }
    implementation { extendsFrom(shade) }
}

tasks {
    jar {
        dependsOn(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(shade.map { zipTree(it) })
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