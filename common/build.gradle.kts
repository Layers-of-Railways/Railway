loom {
    accessWidenerPath = file("src/main/resources/railways.accesswidener")
}

architectury {
    common {
        for(p in rootProject.subprojects) {
            if(p != project) {
                this@common.add(p.name)
            }
        }
    }
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
    modCompileOnly("maven.modrinth:sodium:${"sodium_version"()}")

    // mod compat for tracks

    // Hex Casting
/*    modCompileOnly("at.petra-k.paucal:paucal-common-${minecraft_version}:${paucal_version}")
    modCompileOnly("at.petra-k.hexcasting:hexcasting-common-${minecraft_version}:${hexcasting_version}")
    modCompileOnly("vazkii.patchouli:Patchouli-xplat:${minecraft_version}-${patchouli_version}")*/

    annotationProcessor(implementation("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")!!)
}

tasks.processResources {
    // must be part of primary mod to be findable
    exclude("resourcepacks/")

    // don't add development or to-do files into built jar
    exclude("**/*.bbmodel", "**/*.lnk", "**/*.xcf", "**/*.md", "**/*.txt", "**/*.blend", "**/*.blend1")
}

sourceSets.main {
    resources { // include generated resources in resources
        srcDir("src/generated/resources")
        exclude(".cache/**")
        exclude("assets/create/**")
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}