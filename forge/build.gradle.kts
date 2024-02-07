import java.io.ByteArrayOutputStream

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("me.modmuss50.mod-publish-plugin")
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

    forge {
        mixinConfig("railways-common.mixins.json")
        mixinConfig("railways.mixins.json")

        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

architectury {
    platformSetupLoomIde()
    forge()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentForge: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentForge.extendsFrom(common)
}
repositories {
    // mavens for Forge-exclusives
    maven { url = uri("https://maven.theillusivec4.top/") } // Curios
    maven { // Create Forge and Registrate Forge
        url = uri("https://maven.tterrag.com/")
        content {
            includeGroup("com.tterrag.registrate")
            includeGroup("com.simibubi.create")
        }
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
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroupByRegex("com.github.*")
        }
    } // Pehkui (Hex Casting dependency)
    maven { url = uri("https://maven.jamieswhiteshirt.com/libs-release") } // Reach Entity Attributes (Hex Casting dependency)
    // Add KFF Maven repository (Hex Casting dependency)
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }

    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    } // Biomes O' Plenty
}

dependencies {
    forge("net.minecraftforge:forge:${"minecraft_version"()}-${"forge_version"()}")
    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionForge")) { isTransitive = false }

    // Create and its dependencies
    modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
    modImplementation("com.tterrag.registrate:Registrate:${"registrate_forge_version"()}")
    modImplementation("com.jozufozu.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_forge_version"()}")

    // Development QOL
//    modLocalRuntime("mezz.jei:jei-${minecraft_version}-forge:${jei_forge_version}")

    // if you would like to add integration with JEI, uncomment this line.
//    modCompileOnly("mezz.jei:jei-${minecraft_version}:${jei_forge_version}:api")

    // Test with JourneyMap in dev
    modLocalRuntime("maven.modrinth:journeymap:${"journeymap_version"()}-forge")
    modCompileOnly("info.journeymap:journeymap-api:${"journeymap_api_version"()}-SNAPSHOT") // for some reason this is needed explicitly

    modCompileOnly("de.maxhenkel.voicechat:voicechat-api:${"voicechat_api_version"()}")

    if ("enable_simple_voice_chat"().toBoolean()) {
        modLocalRuntime("maven.modrinth:simple-voice-chat:forge-${"voicechat_version"()}")
    }

    // mod compat for tracks
    if ("enable_hexcasting"().toBoolean()) {
        modLocalRuntime("at.petra-k.paucal:paucal-forge-${"minecraft_version"()}:${"paucal_version"()}")
        modLocalRuntime("at.petra-k.hexcasting:hexcasting-forge-${"minecraft_version"()}:${"hexcasting_version"()}") {
            exclude(group = "com.github.Virtuoel", module = "Pehkui")
            exclude(group = "net.minecraftforge", module = "forge")
            exclude(group = "top.theillusivec4.curios", module = "curios-forge")
            exclude(group = "mezz.jei", module = "jei-1.19.2-forge")
        }
        //modApi("com.github.Virtuoel:Pehkui:${pehkui_version}-${minecraft_version}-forge") // probably not needed
        modLocalRuntime("vazkii.patchouli:Patchouli:${"minecraft_version"()}-${"patchouli_version"()}")
        modLocalRuntime("thedarkcolour:kotlinforforge:${"kotlin_for_forge_version"()}")
    }

    if ("enable_byg"().toBoolean()) {
        modLocalRuntime("maven.modrinth:biomesyougo:${"byg_version"()}-forge")
    }
    if ("enable_byg"().toBoolean() || "enable_bop"().toBoolean()) {
        modLocalRuntime("maven.modrinth:terrablender:${"terrablender_version_forge"()}")
    }
    if ("enable_bop"().toBoolean()) {
        modLocalRuntime("curse.maven:biomesoplenty-220318:${"bop_version"()}")
    }
    if ("enable_dnd"().toBoolean()) {
        modLocalRuntime("maven.modrinth:create-dreams-and-desires:${"dnd_version"()}")
    }
    if ("enable_quark"().toBoolean()) {
        modLocalRuntime("maven.modrinth:quark:${"minecraft_version"()}-${"quark_version"()}")
        modLocalRuntime("vazkii.autoreglib:AutoRegLib:${"arl_version"()}")
    }

    if ("enable_sodium_rubidium"().toBoolean()) {
        modLocalRuntime("maven.modrinth:rubidium:${"rubidium_version"()}")
    }

    if ("enable_eb"().toBoolean()) {
        modImplementation("com.rabbitminers:extendedbogeys-forge:${"EB_verison"()}+forge-patch-")
    }

    if ("enable_sc"().toBoolean()) {
        modLocalRuntime("curse.maven:securitycraft-64760:${"sc_version"()}")
    }

    val mixinExtras = "io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}"

    compileOnly("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
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
            "forge_version" to "forge_version"().split("\\.")[0], // only specify major version of forge
            "minecraft_version" to "minecraft_version"(),
            "create_version" to "create_forge_version"().split("-")[0], // cut off build number
            "voicechat_api_version" to "voicechat_api_version"()
    )

    properties.forEach { (k, v) -> inputs.property(k, v) }

    filesMatching("META-INF/mods.toml") {
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
    exclude("fabric.mod.json")
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
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
    displayName = "Steam 'n' Rails ${"mod_version"()} Forge ${"minecraft_version"()}"
    modLoaders.add("forge")
    modLoaders.add("neoforge")

    curseforge {
        projectId = "curseforge_id"()
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        minecraftVersions.add("minecraft_version"())

        requires {
            slug = "create"
        }
    }

    modrinth {
        projectId = "modrinth_id"()
        accessToken = System.getenv("MODRINTH_TOKEN")
        minecraftVersions.add("minecraft_version"())

        requires {
            slug = "create"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenForge") {
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
