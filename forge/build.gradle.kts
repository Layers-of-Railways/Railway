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

plugins {
    id("net.neoforged.gradle.userdev")
    id("net.kyori.blossom")
}

operator fun String.invoke(): String = rootProject.ext[this] as? String ?: error("Property $this not found")

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.createmod.net")
    maven("https://mvn.devos.one/snapshots/")
    maven("https://maven.blamejared.com/")
    maven("https://maven.tterrag.com/")
}



dependencies {
    implementation("net.neoforged:neoforge:${"neoforge_version"()}")

    // Create and its dependencies (NeoForge)
    implementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}")
    
    // Catnip - Create utility library (must be added explicitly, shaded into Create)
    implementation("net.createmod.catnip:Catnip-NeoForge-${"minecraft_version"()}:${"catnip_version"()}")
    
    // Ponder - Create's in-game documentation system (must be added explicitly)
    implementation("net.createmod.ponder:Ponder-NeoForge-${"minecraft_version"()}:${"ponder_version"()}")
    
    // Flywheel - rendering engine (must be added explicitly)
    implementation("dev.engine-room.flywheel:flywheel-neoforge-${"minecraft_version"()}:${"flywheel_version"()}")
    
    // Registrate
    implementation("com.tterrag.registrate:Registrate:${"registrate_forge_version"()}")
    
    // MixinExtras
    implementation("io.github.llamalad7:mixinextras-neoforge:${"mixin_extras_version"()}")
    
    // Annotations
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    
    // Note: @ExpectPlatform from Architectury no longer used
    // Platform-specific implementations are directly in forge/ package
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version.toString(),
        "minecraft_version" to "minecraft_version"(),
        "neoforge_version" to "neoforge_version"(),
        "mod_id" to "mod_id"(),
        "mod_name" to "mod_name"()
    )
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") { expand(props) }
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Specification-Title" to "railways",
            "Implementation-Version" to project.version
        ))
    }
}
