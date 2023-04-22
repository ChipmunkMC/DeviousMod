plugins {
    id("java")
    id("checkstyle")
    id("fabric-loom") version "1.0-SNAPSHOT"
}

java.sourceCompatibility = JavaVersion.VERSION_17

project.version = project.property("mod_version")!!
project.group = project.property("maven_group")!!

loom {
    accessWidenerPath.set(file("src/main/resources/deviousmod.accesswidener"))
}

repositories {
    mavenCentral()

    maven("https://maven.gegy.dev/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://jitpack.io/")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")

    /** include() shades the dependency in the mod jar.
     * NOTE: You must include EVERY dependency or else it won't launch!
     **/
    implementation("com.github.steveice10:opennbt:1.5-SNAPSHOT")?.let { include(it) }
    implementation("com.github.allinkdev:Reflector:1.1.1")?.let { include(it) }
    modImplementation("net.kyori:adventure-platform-fabric:5.8.0")?.let { include(it) }
}

tasks {
    assemble {
        dependsOn(checkstyleMain)
    }

    test {
        useJUnitPlatform()
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(project.properties)
        }
    }

    jar {
        from("LICENSE") {
            rename { "LICENSE_${archiveBaseName.get()}" }
        }
    }
}