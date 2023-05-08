plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.github.allinkdev.deviousmod"
version = "3.0.0-SNAPSHOT"
description = "Minecraft-independent Java 8+ API exposing silhouettes of DeviousMod classes"
java.sourceCompatibility = JavaVersion.VERSION_1_8

java {
    withSourcesJar()
    withJavadocJar()
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