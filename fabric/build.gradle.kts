import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.configuration.FabricApiExtension.DataGenerationSettings
import java.io.ByteArrayOutputStream

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("me.modmuss50.mod-publish-plugin")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    val common = project(":common")
    accessWidenerPath = common.loom.accessWidenerPath

    silentMojangMappingsLicense()
    runs.configureEach {
        vmArg("-Dmixin.debug.export=true")
        vmArg("-Dmixin.env.remapRefMap=true")
        vmArg("-Dmixin.env.refMapRemappingFile=${projectDir}/build/createSrgToMcp/output.srg")
    }


    runs {
        create("datagen") {
            client()

            name = "Minecraft Data"
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${common.file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=railways")
            vmArg("-Dporting_lib.datagen.existing_resources=${common.file("src/main/resources")}")

            environmentVariable("DATAGEN", "TRUE")
        }
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentFabric: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentFabric.extendsFrom(common)
}

repositories {
    // mavens for Fabric-exclusives
    maven { url = uri("https://maven.terraformersmc.com/releases/") } // Mod Menu, EMI
    maven { url = uri("https://mvn.devos.one/snapshots/") } // Create Fabric, Porting Lib, Forge Tags, Milk Lib, Registrate Fabric
    maven { url = uri("https://cursemaven.com") } // Forge Config API Port
    maven { url = uri("https://maven.cafeteria.dev/releases") } // Fake Player API
    maven { url = uri("https://maven.jamieswhiteshirt.com/libs-release") } // Reach Entity Attributes
    maven { url = uri("https://jitpack.io/") } // Mixin Extras, Fabric ASM
    maven { // forge config api port
        name = "Fuzs Mod Resources"
        url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
    }
    maven {
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("at.petra-k")
            includeGroup("vazkii.patchouli")
        }
    } // JEI, Hex Casting
    maven {
        name = "Ladysnake Mods"
        url = uri("https://maven.ladysnake.org/releases")
        content {
            includeGroup("dev.onyxstudios.cardinal-components-api")
        }
    } // Cardinal Components (Hex Casting dependency)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }

    // dependencies
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")

    // Create - dependencies are added transitively
    modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}")

    // Fabric ASM (enum extension etc)
    modImplementation("com.github.Chocohead:Fabric-ASM:v2.3") {
        exclude (group = "net.fabricmc.fabric-api")
    }

    include("com.github.Chocohead:Fabric-ASM:v2.3")

    // Development QOL
    modLocalRuntime("maven.modrinth:lazydfu:${"lazydfu_version"()}")
    modLocalRuntime("com.terraformersmc:modmenu:${"modmenu_version"()}")

    modLocalRuntime("dev.emi:emi-fabric:${"emi_version"()}")

    modLocalRuntime("maven.modrinth:journeymap:${"journeymap_version"()}-fabric") // Test with JourneyMap in dev
    modLocalRuntime("info.journeymap:journeymap-api:${"journeymap_api_version"()}-fabric-SNAPSHOT") // API is a JiJ on fabric, add manually

    modCompileOnly("info.journeymap:journeymap-api:${"journeymap_api_version"()}-fabric-SNAPSHOT") // for some reason this is needed explicitly

    modCompileOnly("de.maxhenkel.voicechat:voicechat-api:${"voicechat_api_version"()}")

    if ("enable_simple_voice_chat"().toBoolean()) {
        modLocalRuntime("maven.modrinth:simple-voice-chat:fabric-${"voicechat_version"()}")
    }

    // because create fabric is a bit broken I think
    modImplementation("net.minecraftforge:forgeconfigapiport-fabric:4.2.9")

    // mod compat for tracks
    if ("enable_hexcasting"().toBoolean()) {
        modLocalRuntime("at.petra-k.paucal:paucal-fabric-${"minecraft_version"()}:${"paucal_version"()}")
        modLocalRuntime("at.petra-k.hexcasting:hexcasting-fabric-${"minecraft_version"()}:${"hexcasting_version"()}")
        modLocalRuntime("vazkii.patchouli:Patchouli:${"minecraft_version"()}-${"patchouli_version"()}-FABRIC")
    }

    if ("enable_byg"().toBoolean()) {
        modLocalRuntime("maven.modrinth:biomesyougo:${"byg_version"()}-fabric")
        modLocalRuntime("maven.modrinth:terrablender:${"terrablender_version_fabric"()}")
        modLocalRuntime("maven.modrinth:geckolib:${"geckolib_version_fabric"()}")
        modLocalRuntime("maven.modrinth:corgilib:${"corgilib_version_fabric"()}")
    }

    if ("enable_tweakeroo"().toBoolean()) {
        modLocalRuntime("curse.maven:tweakeroo-297344:${"tweakeroo_version"()}")
        modLocalRuntime("curse.maven:malilib-303119:${"malilib_version"()}")
    }

    if ("enable_sodium_rubidium"().toBoolean()) {
        modLocalRuntime("maven.modrinth:sodium:${"sodium_version"()}")
        modLocalRuntime("org.joml:joml:1.10.2")
        modLocalRuntime("maven.modrinth:indium:${"indium_version"()}")
    }
    if ("enable_iris"().toBoolean()) {
        modLocalRuntime("maven.modrinth:iris:${"iris_version"()}")
    }

    if ("enable_eb"().toBoolean()) {
        modImplementation("com.rabbitminers:extendedbogeys-fabric:${"EB_verison"()}+fabric")
    }

    val mixinExtras = "io.github.llamalad7:mixinextras-fabric:${"mixin_extras_version"()}"

    annotationProcessor(mixinExtras)
    implementation(mixinExtras)
    include(mixinExtras)
}

tasks.processResources {
    // include packs
    from(rootProject.file("common/src/main/resources")) {
        include("resourcepacks/")
    }

    // set up properties for filling into metadata
    val properties = mapOf(
            "version" to version,
            "fabric_loader_version" to "fabric_loader_version"(),
            "fabric_api_version" to "fabric_api_version"(),
            "minecraft_version" to "minecraft_version"(),
            "create_version" to "create_fabric_version"().split("\\+")[0], // Trim +mcX.XX.X from version string
            "voicechat_api_version" to "voicechat_api_version"()
    )

    properties.forEach { (k, v) -> inputs.property(k, v) }

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

val getGitHash = { ->
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

val hasUnstaged = { ->
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "status", "--porcelain")
        standardOutput = stdout
    }
    val result = stdout.toString().replace("M gradlew", "").trim()
    if (result.isNotEmpty())
        println("Found stageable results:\n${result}\n")
    result.isNotEmpty()
}

tasks.shadowJar {
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier = null
}

tasks.jar {
    archiveClassifier = "dev"

    val gitHash = "\"${getGitHash()}" + (if (hasUnstaged()) "-modified" else "") + "\""

    manifest {
        attributes(mapOf("Git-Hash" to gitHash))
    }
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })

    val gitHash =  "\"${getGitHash()}" + (if (hasUnstaged()) "-modified" else "") + "\""

    manifest {
        attributes(mapOf("Git-Hash" to gitHash))
    }
}

components.getByName("java") {
    this as AdhocComponentWithVariants
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}

publishMods {
    file(tasks.remapJar.get().archiveFile)
    version.set(project.version.toString())
    changelog = dev.ithundxr.silk.ChangelogText.getChangelogText(rootProject).toString()
    type = STABLE
    displayName = "Steam 'n' Rails ${"mod_version"()} Fabric ${"minecraft_version"()}"
    modLoaders.add("fabric")
    modLoaders.add("quilt")

    curseforge {
        projectId = "curseforge_id"()
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        minecraftVersions.add("minecraft_version"())

        requires {
            slug = "create-fabric"
        }
    }

    modrinth {
        projectId = "modrinth_id"()
        accessToken = System.getenv("MODRINTH_TOKEN")
        minecraftVersions.add("minecraft_version"())

        requires {
            slug = "create-fabric"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenFabric") {
            artifactId = "${"archives_base_name"()}-${project.name}-${"minecraft_version"()}"
            from(components["java"])
        }
    }

    repositories {
        if (System.getenv("MAVEN_TOKEN") != null) {
            if (System.getenv("RELEASE_BUILD")?.toBoolean() == true) {
                maven {
                    url = uri("https://maven.ithundxr.dev/releases")
                    credentials {
                        username = "railways-github"
                        password = System.getenv("MAVEN_TOKEN")
                    }
                }
            } else {
                maven {
                    url = uri("https://maven.ithundxr.dev/snapshots")
                    credentials {
                        username = "railways-github"
                        password = System.getenv("MAVEN_TOKEN")
                    }
                }
            }
        }
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}