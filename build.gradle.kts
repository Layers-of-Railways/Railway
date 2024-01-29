import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.4.+" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.3.4" apply false // https://github.com/modmuss50/mod-publish-plugin
    id("dev.ithundxr.silk") version "0.11.15" // https://github.com/IThundxr/silk
}

architectury {
    minecraft = "minecraft_version"()
}

subprojects {
    apply(plugin = "dev.architectury.loom")

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

    dependencies {
        "minecraft"("com.mojang:minecraft:${"minecraft_version"()}")
        // layered mappings - Mojmap names, parchment and QM docs and parameters
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").layered {
            mappings("org.quiltmc:quilt-mappings:${"minecraft_version"()}+build.${"qm_version"()}:intermediary-v2")
            parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
            officialMojangMappings { nameSyntheticMembers = false }
        })
    }

    tasks.register<Copy>("moveBuiltJars") {
        if (project.path != ":common") {
            val remapJar by project.tasks.named<Jar>("remapJar")
            dependsOn(remapJar)
            from(remapJar)
        }

        into(rootProject.file("jars"))
    }
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

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}