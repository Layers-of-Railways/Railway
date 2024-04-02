import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.configurationcache.extensions.capitalized
import java.io.ByteArrayOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

plugins {
    java
    `maven-publish`
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "1.5.+" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.3.4" apply false // https://github.com/modmuss50/mod-publish-plugin
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("dev.ithundxr.silk") version "0.11.15" // https://github.com/IThundxr/silk
}

println("Steam 'n' Rails v${"mod_version"()}")
apply(plugin = "architectury-plugin")

architectury {
    minecraft = "minecraft_version"()
}

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()
val gitHash = "\"${calculateGitHash() + (if (hasUnstaged()) "-modified" else "")}\""

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
    val build = buildNumber?.let { "-build.${it}" } ?: "-local"

    version = "${"mod_version"()}+${project.name}-mc${"minecraft_version"() + if (isRelease) "" else build}"

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    java {
        withSourcesJar()
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    setupRepositories()

    val capitalizedName = project.name.capitalized()

    val loom = project.extensions.getByType<LoomGradleExtensionAPI>()
    loom.apply {
        silentMojangMappingsLicense()
        runs.configureEach {
            vmArg("-Dmixin.debug.export=true")
            vmArg("-Dmixin.env.remapRefMap=true")
            vmArg("-Dmixin.env.refMapRemappingFile=${projectDir}/build/createSrgToMcp/output.srg")
        }
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

    publishing {
        publications {
            create<MavenPublication>("maven${capitalizedName}") {
                artifactId = "${"archives_base_name"()}-${project.name}-${"minecraft_version"()}"
                from(components["java"])
            }
        }

        repositories {
            val mavenToken = System.getenv("MAVEN_TOKEN")
            if (mavenToken != null && mavenToken.isNotEmpty()) {
                if (isRelease) {
                    maven {
                        url = uri("https://maven.ithundxr.dev/releases")
                        credentials {
                            username = "railways-github"
                            password = mavenToken
                        }
                    }
                } else {
                    maven {
                        url = uri("https://maven.ithundxr.dev/snapshots")
                        credentials {
                            username = "railways-github"
                            password = mavenToken
                        }
                    }
                }
            }
        }
    }

    // from here down is platform configuration
    if(project.path == ":common") {
        return@subprojects
    }

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    architectury {
        platformSetupLoomIde()
    }

    tasks.named<RemapJarTask>("remapJar") {
        val shadowJar = project.tasks.named<ShadowJar>("shadowJar").get()
        inputFile.set(shadowJar.archiveFile)
        injectAccessWidener = true
        dependsOn(shadowJar)
        archiveClassifier = null
        doLast {
            squishJar(outputs.files.singleFile)
        }
    }

    val common: Configuration by configurations.creating
    val shadowCommon: Configuration by configurations.creating
    val development = configurations.maybeCreate("development${capitalizedName}")

    configurations {
        compileOnly.get().extendsFrom(common)
        runtimeOnly.get().extendsFrom(common)
        development.extendsFrom(common)
    }

    dependencies {
        common(project(":common", "namedElements")) { isTransitive = false }
        shadowCommon(project(":common", "transformProduction${capitalizedName}")) { isTransitive = false }
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier = "dev-shadow"
        configurations = listOf(shadowCommon)
        exclude("architectury.common.json")
        destinationDirectory = layout.buildDirectory.dir("devlibs").get()
    }

    tasks.processResources {
        // include packs
        from(project(":common").file("src/main/resources")) {
            include("resourcepacks/")
        }

        // set up properties for filling into metadata
        val properties = mapOf(
                "version" to version,
                "minecraft_version" to "minecraft_version"(),
                "fabric_api_version" to "fabric_api_version"(),
                "fabric_loader_version" to "fabric_loader_version"(),
                "voicechat_api_version" to "voicechat_api_version"(),
                "forge_version" to "forge_version"().split(".")[0], // only specify major version of forge
                "create_forge_version" to "create_forge_version"().split("-")[0], // cut off build number
                "create_fabric_version" to "create_fabric_version"().split("+")[0] // Trim +mcX.XX.X from version string
        )

        inputs.properties(properties)

        filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
            expand(properties)
        }
    }

    tasks.jar {
        archiveClassifier = "dev"

        manifest {
            attributes(mapOf("Git-Hash" to gitHash))
        }
    }

    tasks.named<Jar>("sourcesJar") {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })

        manifest {
            attributes(mapOf("Git-Hash" to gitHash))
        }
    }

    components.getByName<AdhocComponentWithVariants>("java") {
        withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
            skip()
        }
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
        contents.forEach { var (name, data) = it
            if(name.startsWith("architectury_inject_${project.name}_common"))
                return@forEach

            if (name.endsWith(".json") || name.endsWith(".mcmeta")) {
                data = (JsonOutput.toJson(JsonSlurper().parse(data)).toByteArray())
            }

            out.putNextEntry(JarEntry(name))
            out.write(data)
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

fun Project.setupRepositories() {
    repositories {
        mavenCentral()
        maven("https://maven.shedaniel.me/") // Cloth Config, REI
        maven("https://maven.blamejared.com/") // JEI, Hex Casting
        exclusiveMaven("https://maven.parchmentmc.org", "org.parchmentmc.data") // Parchment mappings
        exclusiveMaven("https://maven.quiltmc.org/repository/release", "org.quiltmc") // Quilt Mappings
        maven("https://jm.gserv.me/repository/maven-public/") // JourneyMap API
        exclusiveMaven("https://api.modrinth.com/maven", "maven.modrinth") // LazyDFU, JourneyMap
        exclusiveMaven("https://cursemaven.com", "curse.maven")
        maven("https://maven.theillusivec4.top/") // Curios
        maven("https://maven.tterrag.com/") { // Flywheel, Registrate, Create
            content {
                includeGroup("com.simibubi.create")
                includeGroup("com.tterrag.registrate")
                includeGroup("com.jozufozu.flywheel")
            }
        }
        maven("https://maven.maxhenkel.de/repository/public") // Simple Voice Chat
        maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
        exclusiveMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour") // KFF (Hex Casting dependency)
        maven("https://maven.terraformersmc.com/releases/") // Mod Menu, EMI
        maven("https://mvn.devos.one/snapshots/") // Create Fabric, Porting Lib, Forge Tags, Milk Lib, Registrate Fabric
        maven("https://mvn.devos.one/releases/") // Porting Lib
        maven("https://maven.cafeteria.dev/releases") // Fake Player API
        maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // forge config api port
        exclusiveMaven("https://maven.ladysnake.org/releases", "dev.onyxstudios.cardinal-components-api") // Cardinal Components (Hex Casting dependency)
        maven("https://jitpack.io/") { // Mixin Extras, Fabric ASM
            content {
                includeGroupByRegex("com.github.*")
            }
        }
    }
}

fun calculateGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun hasUnstaged(): Boolean {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "status", "--porcelain")
        standardOutput = stdout
    }
    val result = stdout.toString().replace("M gradlew", "").trimEnd()
    if (result.isNotEmpty())
        println("Found stageable results:\n${result}\n")
    return result.isNotEmpty()
}

fun Project.architectury(action: Action<ArchitectPluginExtension>) {
    action.execute(this.extensions.getByType<ArchitectPluginExtension>())
}

fun RepositoryHandler.exclusiveMaven(url: String, vararg groups: String) {
    exclusiveContent {
        forRepository { maven(url) }
        filter {
            groups.forEach {
                includeGroup(it)
            }
        }
    }
}