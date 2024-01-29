import groovy.json.JsonOutput
import groovy.json.JsonSlurper

loom {
    accessWidenerPath = file("src/main/resources/railways.accesswidener")
}

repositories {
    // mavens for Create Fabric and dependencies
    maven { url = uri("https://api.modrinth.com/maven") } // LazyDFU, Journyemap
    maven { url = uri("https://maven.terraformersmc.com/releases/") } // Mod Menu
    maven { url = uri("https://mvn.devos.one/snapshots/") } // Create Fabric, Porting Lib, Forge Tags, Milk Lib, Registrate Fabric
    maven { url = uri("https://cursemaven.com") } // Forge Config API Port
    maven { url = uri("https://maven.cafeteria.dev/releases") } // Fake Player API
    maven { url = uri("https://maven.jamieswhiteshirt.com/libs-release") } // Reach Entity Attributes
    maven { url = uri("https://jitpack.io/") } // Mixin Extras, Fabric ASM
    maven { url = uri("https://maven.blamejared.com/") } // JEI, Hex Casting
}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    // Compile against Create Fabric in common
    // beware of differences across platforms!
    // dependencies must also be pulled in to minimize problems, from remapping issues to compile errors.
    // All dependencies except Flywheel and Registrate are NOT safe to use!
    // Flywheel and Registrate must also be used carefully due to differences.
    modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}")

    // required for proper remapping and compiling
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")

    // JourneyMap compat
    modCompileOnly("info.journeymap:journeymap-api:${"journeymap_api_version"()}-fabric-SNAPSHOT")

    modCompileOnly("de.maxhenkel.voicechat:voicechat-api:${"voicechat_api_version"()}")
    modCompileOnly("maven.modrinth:simple-voice-chat:fabric-${"voicechat_version"()}")
    modCompileOnly("curse.maven:malilib-303119:${"malilib_version"()}")
    modCompileOnly("curse.maven:tweakeroo-297344:${"tweakeroo_version"()}")
    modCompileOnly("maven.modrinth:sodium:${"sodium_version"()}")

    // mod compat for tracks

    // Hex Casting
/*    modCompileOnly("at.petra-k.paucal:paucal-common-${minecraft_version}:${paucal_version}")
    modCompileOnly("at.petra-k.hexcasting:hexcasting-common-${minecraft_version}:${hexcasting_version}")
    modCompileOnly("vazkii.patchouli:Patchouli-xplat:${minecraft_version}-${patchouli_version}")*/

    val mixinExtras = "io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}"

    annotationProcessor(mixinExtras)
    implementation(mixinExtras)
}

architectury {
    common(rootProject.property("enabled_platforms").toString().split(","))
}

tasks.processResources {
    // must be part of primary mod to be findable
    exclude("resourcepacks/")

    // dont add development or to-do files into built jar
    exclude("**/*.bbmodel", "**/*.lnk", "**/*.xcf", "**/*.md", "**/*.txt", "**/*.blend", "**/*.blend1", "**/PlatformMethods.class")

    // Minify all .json files in built jars
    doLast {
        val outputDir = File(outputs.files.asPath)
        outputDir.walkTopDown()
            .filter { it.isFile && it.extension == "json" }
            .forEach { file ->
                val jsonContent = file.readText()
                val parsedJson = JsonSlurper().parseText(jsonContent)
                val updatedJson = JsonOutput.toJson(parsedJson)
                file.writeText(updatedJson)
            }
    }
}

sourceSets.main {
    resources { // include generated resources in resources
        srcDir("src/generated/resources")
        exclude(".cache/**")
        exclude("assets/create/**")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
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