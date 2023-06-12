plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.github.allinkdev"
version = "1.1.0"
description = "Minecraft-independent Java 8+ API exposing silhouettes of DeviousMod classes"
java.sourceCompatibility = JavaVersion.VERSION_1_8

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    compileOnlyApi("com.mojang:brigadier:1.0.18") // This dependency is marked as compile-only as it is expected to be provided in the environment of dependents.
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