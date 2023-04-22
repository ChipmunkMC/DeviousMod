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
    minecraft(libs.minecraft)
    mappings(libs.yarn.mappings)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    implementation(libs.opennbt)?.let { include(it) }
    implementation(libs.reflector)?.let { include(it) }
    modImplementation(libs.adventure.platform.fabric)?.let { include(it) }
}

tasks {
    assemble {
        dependsOn(checkstyleMain)
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