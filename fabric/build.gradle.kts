/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import dev.ithundxr.silk.ChangelogText

architectury.fabric()

loom {
    val common = project(":common")
    accessWidenerPath = common.loom.accessWidenerPath

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

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")

    // Create - dependencies are added transitively
    modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}")

    // Fabric ASM (enum extension etc)
    modImplementation("com.github.Chocohead:Fabric-ASM:v2.3") {
        exclude (group = "net.fabricmc.fabric-api")
    }

    include("com.github.Chocohead:Fabric-ASM:v2.3")

    // Forge Config API Port
    modImplementation(include("net.minecraftforge:forgeconfigapiport-fabric:4.2.9")!!)

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

    if ("enable_natures_spirit"().toBoolean()) {
        modLocalRuntime("maven.modrinth:natures-spirit:${"natures_spirit_version"()}")
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
        modLocalRuntime("org.anarres:jcpp:1.4.14")
        modLocalRuntime("io.github.douira:glsl-transformer:2.0.0-pre13")
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    version.set(project.version.toString())
    changelog = ChangelogText.getChangelogText(rootProject).toString()
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

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}