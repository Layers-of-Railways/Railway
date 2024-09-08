/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.ithundxr.silk.ChangelogText
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import me.modmuss50.mpp.ModPublishExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

plugins {
    id("java")
    id("maven-publish")
    id("dev.ithundxr.silk")
    id("architectury-plugin")
    id("org.jetbrains.gradle.plugin.idea-ext")

    id("net.kyori.blossom") apply false
    id("com.gradleup.shadow") apply false
    id("dev.architectury.loom") apply false
    id("me.modmuss50.mod-publish-plugin") apply false
}

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()
val removeDevMixinAnyway = System.getenv("REMOVE_DEV_MIXIN_ANYWAY")?.toBoolean() ?: false
val gitHash = "\"${calculateGitHash() + (if (hasUnstaged()) "-modified" else "")}\""

extra["gitHash"] = gitHash

idea {
    module {
        isDownloadSources = true
    }
}

architectury {
    minecraft = "minecraft_version"()
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

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "net.kyori.blossom")

    setupRepositories()

    val capitalizedName = project.name.replaceFirstChar { it.uppercase() }

    loom {
        silentMojangMappingsLicense()
        runs.configureEach {
            vmArg("-XX:+AllowEnhancedClassRedefinition")
            vmArg("-XX:+IgnoreUnrecognizedVMOptions")
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

        // Used to decompile mixin dumps, needs to be on the classpath
        // Uncomment if you want it to decompile mixin exports, beware it has very verbose logging.
        //implementation("org.vineflower:vineflower:1.10.0")
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
            val maven = if (isRelease) "releases" else "snapshots"
            if (mavenToken != null && mavenToken.isNotEmpty()) {
                maven("https://maven.ithundxr.dev/${maven}") {
                    credentials {
                        username = "railways-github"
                        password = mavenToken
                    }
                }
            }
        }
    }

    // from here down is platform configuration
    if(project.path == ":common") {
        return@subprojects
    }

    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    architectury {
        platformSetupLoomIde()
    }

    tasks.named<RemapJarTask>("remapJar") {
        from("${rootProject.projectDir}/LICENSE")
        val shadowJar = project.tasks.getByName<ShadowJar>("shadowJar")
        inputFile.set(shadowJar.archiveFile)
        injectAccessWidener = true
        dependsOn(shadowJar)
        archiveClassifier = null
        doLast {
            transformJar(outputs.files.singleFile)
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

        // Trim -build.X+mcX.XX.X from version string
        //val createFabricVersion: String = Regex("(\\d+\\.\\d+\\.\\d+-\\w)").find("create_fabric_version"())?.value.toString()

        // set up properties for filling into metadata
        val properties = mapOf(
                "version" to version,
                "minecraft_version" to "minecraft_version"(),
                "fabric_api_version" to "fabric_api_version"(),
                "fabric_loader_version" to "fabric_loader_version"(),
                "voicechat_api_version" to "voicechat_api_version"(),
                "forge_version" to "forge_version"().split(".")[0], // only specify major version of forge
                "create_forge_version" to "create_forge_version"().split("-")[0],
                "create_fabric_version" to "create_fabric_version"()
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

    publishMods {
        file = tasks.getByName<RemapJarTask>("remapJar").archiveFile
        version.set(project.version.toString())
        changelog = ChangelogText.getChangelogText(rootProject).toString()
        type = STABLE
        displayName = "Steam 'n' Rails ${"mod_version"()} ${capitalizedName} ${"minecraft_version"()}"

        curseforge {
            projectId = "curseforge_id"()
            accessToken = System.getenv("CURSEFORGE_TOKEN")
            minecraftVersions.add("minecraft_version"())
        }

        modrinth {
            projectId = "modrinth_id"()
            accessToken = System.getenv("MODRINTH_TOKEN")
            minecraftVersions.add("minecraft_version"())
        }
    }
}

fun transformJar(jar: File) {
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
            } else if (name.endsWith(".class")) {
                data = transformClass(data)
            }

            out.putNextEntry(JarEntry(name))
            out.write(data)
            out.closeEntry()
        }
        out.finish()
        out.close()
    }
}

fun transformClass(bytes: ByteArray): ByteArray {
    val node = ClassNode()
    ClassReader(bytes).accept(node, 0)

    // Remove Methods & Field Annotated with @DevEnvMixin
    node.methods.removeIf { methodNode: MethodNode -> removeIfDevMixin(node.name, methodNode.visibleAnnotations) }
    // Disabled as I don't feel ok with people being able to remove these
    //node.fields.removeIf { fieldNode: FieldNode -> removeIfDevMixin(fieldNode.visibleAnnotations) }

    return ClassWriter(0).also { node.accept(it) }.toByteArray()
}

fun removeIfDevMixin(nodeName: String, visibleAnnotations: List<AnnotationNode>?): Boolean {
    // Don't remove methods if it's not a GHA build/Release build
    if (!removeDevMixinAnyway && buildNumber == null && !nodeName.lowercase(Locale.ROOT).matches(Regex(".*\\/mixin\\/.*Mixin")))
        return false

    if (visibleAnnotations != null) {
        for (annotationNode in visibleAnnotations) {
            if (annotationNode.desc == "Lcom/railwayteam/railways/annotation/mixin/DevEnvMixin;")
                return true
        }
    }

    return false
}

fun <T> getValueFromAnnotation(annotation: AnnotationNode?, key: String): T? {
    var getNextValue = false

    if (annotation?.values == null) {
        return null
    }

    // Keys and value are stored in successive pairs, search for the key and if found return the following entry
    for (value in annotation.values) {
        if (getNextValue) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
        if (value == key) {
            getNextValue = true
        }
    }

    return null
}

tasks.create("railwaysPublish") {
    when (val platform = System.getenv("PLATFORM")) {
        "both" -> {
            dependsOn(tasks.build, ":fabric:publish", ":forge:publish", ":common:publish", ":fabric:publishMods", ":forge:publishMods")
        }
        "fabric", "forge" -> {
            dependsOn("${platform}:build", "${platform}:publish", "${platform}:publishMods")
        }
    }
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
    try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "HEAD")
            standardOutput = stdout
        }
        return stdout.toString().trim()
    } catch(ignored: Throwable) {
        return "unknown"
    }
}

fun hasUnstaged(): Boolean {
    try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "status", "--porcelain")
            standardOutput = stdout
        }
        val result = stdout.toString()
            .replace(Regex("M gradlew(\\.bat)?"), "")
            .lineSequence()
            .filter { it.isNotBlank() }
            .joinToString("\n")
        if (result.isNotEmpty())
            println("Found stageable results:\n${result}\n")
        return result.isNotEmpty()
    }  catch(ignored: Throwable) {
        return false
    }
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

val Project.loom: LoomGradleExtensionAPI
    get() = the()
fun Project.loom(block: Action<in LoomGradleExtensionAPI>) = block.execute(the())
fun Project.publishMods(block: Action<in ModPublishExtension>) = block.execute(the())

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this is not defined")

