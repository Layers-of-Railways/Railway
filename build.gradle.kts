import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

plugins {
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.4.+" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.3.4" apply false // https://github.com/modmuss50/mod-publish-plugin
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("dev.ithundxr.silk") version "0.11.15" // https://github.com/IThundxr/silk
}

architectury {
    minecraft = "minecraft_version"()
}

tasks.jar {
    enabled = false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    base.archivesName.set("archives_base_name"())
    group = "maven_group"()

    // Formats the mod version to include the loader, Minecraft version, and build number (if present)
    // example: 1.0.0+fabric-1.19.2-build.100 (or -local)
    val isRelease: Boolean = System.getenv("RELEASE_BUILD")?.toBoolean() == true
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER")
    val build = if (buildNumber != null) "build.${buildNumber}" else "local"

    version = "${"mod_version"()}+${project.name}-mc${"minecraft_version"()}" + ( if (isRelease) "" else "-${build}")

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    java {
        withSourcesJar()
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = project.extensions.getByType<LoomGradleExtensionAPI>()
    loom.silentMojangMappingsLicense()

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.shedaniel.me/") } // Cloth Config, REI
        maven { url = uri("https://maven.blamejared.com/") } // JEI, Hex Casting
        maven { url = uri("https://maven.parchmentmc.org") } // Parchment mappings
        maven { url = uri("https://maven.quiltmc.org/repository/release") } // Quilt Mappings
        maven { url = uri("https://jm.gserv.me/repository/maven-public/") } // JourneyMap API
        maven { url = uri("https://api.modrinth.com/maven") } // LazyDFU, JourneyMap
        maven { // Flywheel
            url = uri("https://maven.tterrag.com/")
            content {
                // need to be specific here due to version overlaps
                includeGroup("com.jozufozu.flywheel")
            }
        }
        maven { // Extended Bogeys
            url = uri("https://maven.ithundxr.dev/private")
            content { includeGroup("com.rabbitminers") }
            credentials {
                if (System.getenv("GITHUB_RUN_NUMBER") != null) {
                    username = "railways-github"
                    password = System.getenv("MAVEN_TOKEN")
                } else {
                    username = findProperty("IThundxrMavenUsername").toString()
                    password = findProperty("IThundxrMavenPassword").toString()
                }
            }
        }
        maven { url = uri("https://maven.maxhenkel.de/repository/public") } // Simple Voice Chat
    }

    configurations.configureEach {
        resolutionStrategy {
            force("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
        }
    }

    @Suppress("UnstableApiUsage")
    dependencies {
        "minecraft"("com.mojang:minecraft:${"minecraft_version"()}")
        // layered mappings - Mojmap names, parchment and QM docs and parameters
        "mappings"(loom.layered {
            mappings("org.quiltmc:quilt-mappings:${"minecraft_version"()}+build.${"qm_version"()}:intermediary-v2")
            parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
            officialMojangMappings { nameSyntheticMembers = false }
        })
    }

    tasks.register<Copy>("moveBuiltJars") {
        if (project.path != ":common") {
            val remapJar by project.tasks.named<RemapJarTask>("remapJar")
            dependsOn(remapJar)
            from(remapJar)
        }

        into(rootProject.file("jars"))
    }

    if(project.path == ":common") {
        return@subprojects
    }

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "me.modmuss50.mod-publish-plugin")
    project.extensions.getByType<ArchitectPluginExtension>().platformSetupLoomIde()

    tasks.named<RemapJarTask>("remapJar") {
        doLast {
            squishJar(inputFile.get().asFile)
        }
    }

    tasks.named<ShadowJar>("shadowJar") {
    }
}

fun squishJar(jar: File) {
    val contents = linkedMapOf<String, ByteArray>()
    JarFile(jar).use {
        it.entries().asIterator().forEach { entry ->
            if (!entry.isDirectory) {
                contents[entry.name] = it.getInputStream(entry).readAllBytes()
            }
        }
    }

    jar.delete()

    JarOutputStream(jar.outputStream()).use { out ->
        out.setLevel(Deflater.BEST_COMPRESSION)
        contents.forEach {
            out.putNextEntry(JarEntry(it.key))
            if (name.endsWith(".json") || name.endsWith(".mcmeta")) {
                out.write(JsonOutput.toJson(JsonSlurper().parse(it.value)).toByteArray())
            } else {
                out.write(it.value)
            }
            out.closeEntry()
        }
        out.finish()
        out.close()
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}