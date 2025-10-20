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
    maven("https://jitpack.io")
    maven("https://maven.parchmentmc.org")
    maven("https://modmaven.dev/")
    maven("https://maven.theillusivec4.top/")
}

dependencies {
    implementation("net.neoforged:neoforge:${"neoforge_version"()}")

    // Create and its dependencies (compileOnly for common, implementation in forge)
    compileOnly("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}")
    
    // Catnip - Create utility library (compileOnly for common)
    compileOnly("net.createmod.catnip:Catnip-NeoForge-${"minecraft_version"()}:${"catnip_version"()}")
    
    // Ponder - Create's in-game documentation system (compileOnly for common)
    compileOnly("net.createmod.ponder:Ponder-NeoForge-${"minecraft_version"()}:${"ponder_version"()}")
    
    // Flywheel - rendering engine (compileOnly for common)
    compileOnly("dev.engine-room.flywheel:flywheel-neoforge-${"minecraft_version"()}:${"flywheel_version"()}")
    
    // Registrate (needed for Create integration)
    compileOnly("com.tterrag.registrate:Registrate:${"registrate_forge_version"()}")
    
    // JSR-305 annotations (javax.annotation.*)
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    // JourneyMap compat
    compileOnly("info.journeymap:journeymap-api:${"journeymap_api_version"()}-fabric-SNAPSHOT")

    // Voice chat compat
    compileOnly("de.maxhenkel.voicechat:voicechat-api:${"voicechat_api_version"()}")
    compileOnly("maven.modrinth:simple-voice-chat:fabric-${"voicechat_version"()}")

    // MixinExtras
    implementation("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
}

sourceSets.main {
    resources {
        // include generated resources in resources
        srcDir("src/generated/resources")
        exclude(".cache/**")
        exclude("assets/create/**")
    }
}

tasks.processResources {
    // must be part of primary mod to be findable
    exclude("resourcepacks/")

    // don't add development or to-do files into built jar
    exclude("**/*.bbmodel", "**/*.lnk", "**/*.xcf", "**/*.md", "**/*.txt", "**/*.blend", "**/*.blend1")
}