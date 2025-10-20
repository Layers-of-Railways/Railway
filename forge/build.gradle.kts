/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import dev.ithundxr.silk.ChangelogText
import me.modmuss50.mpp.ReleaseType

architectury.forge()

loom {
    val common = project(":common")
    accessWidenerPath = common.loom.accessWidenerPath

    forge {
        mixinConfig("railways-common.mixins.json")
        mixinConfig("railways.mixins.json")

        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }

    runs.configureEach {
        programArg("-mixin.config=create.mixins.json")
    }
}

dependencies {
    forge("net.minecraftforge:forge:${"minecraft_version"()}-${"forge_version"()}")

    // Create and its dependencies (Forge)
    // Use full (non-slim) artifact to include bundled Flywheel and other dependencies
    modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}") {
        exclude(group = "dev.ftb.mods")
    }
    // Catnip utilities (NeoForge build) required by Create/Ponder
    // Exclude Flywheel since Create provides it
    modImplementation("net.createmod.catnip:Catnip-NeoForge-1.21.1:${"catnip_version"()}") {
        exclude(group = "dev.engine_room.flywheel")
    }
    modImplementation("net.createmod.ponder:Ponder-Forge-${"minecraft_version"()}:${"ponder_version"()}") {
        exclude(group = "dev.engine_room.flywheel", module = "flywheel-neoforge-${"minecraft_version"()}")
    }
    modImplementation("com.tterrag.registrate:Registrate:${"registrate_forge_version"()}")
    // Flywheel does not publish Forge artifacts for 1.21.1 on Create maven; Create 1.21.x handles rendering
    //modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-${"minecraft_version"()}:${"flywheel_version"()}")
    //modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_version"()}")

    // Development QOL (toggleable if resolution causes issues)
    if (System.getenv("ENABLE_EMI")?.toBoolean() != false) {
        modLocalRuntime("dev.emi:emi-forge:${"emi_version"()}")
    }

    // Test with JourneyMap in dev
    if (System.getenv("ENABLE_JM")?.toBoolean() != false) {
        modLocalRuntime("maven.modrinth:journeymap:${"journeymap_version"()}-forge")
    }
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
        modLocalRuntime("org.violetmoon.quark:Quark:${"quark_version"()}")
        modLocalRuntime("org.violetmoon.zeta:zeta:${"zeta_version"()}")
    }

    if ("enable_tfc"().toBoolean()) {
        modLocalRuntime("vazkii.patchouli:Patchouli:${"minecraft_version"()}-${"patchouli_version"()}-FORGE")
        modLocalRuntime("maven.modrinth:terrafirmacraft:${"tfc_version"()}")
    }

    if ("enable_sodium_rubidium"().toBoolean()) {
        modLocalRuntime("maven.modrinth:rubidium:${"rubidium_version"()}")
    }

    if ("enable_sc"().toBoolean()) {
        modLocalRuntime("curse.maven:securitycraft-64760:${"sc_version"()}")
    }

    compileOnly("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
    annotationProcessor(implementation(include("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")!!)!!)
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
