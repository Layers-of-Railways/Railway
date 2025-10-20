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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import me.modmuss50.mpp.ModPublishExtension
import java.util.*
import java.io.ByteArrayOutputStream
import dev.ithundxr.silk.ChangelogText
import me.modmuss50.mpp.ReleaseType

plugins {
    java
    `maven-publish`
    id("net.neoforged.gradle.userdev") version "7.0.152" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.7.4" apply false
    id("dev.ithundxr.silk") version "0.11.15"
    id("net.kyori.blossom") version "2.1.0" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
}

println("Steam 'n' Rails v${"mod_version"()}")

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()
val gitHash = "\"${calculateGitHash() + (if (hasUnstaged()) "-modified" else "")}\""

extra["gitHash"] = gitHash

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    base.archivesName.set("archives_base_name"())
    group = "maven_group"()

    val build = buildNumber?.let { "-build.${it}" } ?: "-local"

    var gitBranchLabel = ""
    if (!isRelease && "mod_version"().endsWith("-alpha")) {
        gitBranchLabel = "-" + calculateGitBranch().replace("/", "_")
    }

    version = "${"mod_version"()}${gitBranchLabel}+neoforge-mc${"minecraft_version"() + if (isRelease) "" else build}"

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    java {
        withSourcesJar()
    }
}

// Repository setup function (called from forge/build.gradle.kts)
fun Project.setupRepositories() {
    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForged"
        }
        maven("https://maven.createmod.net") {
            name = "Create"
        }
        maven("https://mvn.devos.one/snapshots/") {
            name = "Registrate"
        }
        maven("https://maven.blamejared.com/") {
            name = "BlameJared"
        }
        maven("https://maven.tterrag.com/") {
            name = "TterragMaven"
        }
        maven("https://jitpack.io") {
            name = "JitPack"
        }
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
        // Optional mods
        maven("https://modmaven.dev/") {
            name = "ModMaven"
        }
        maven("https://maven.theillusivec4.top/") {
            name = "TheIllusiveC4"
        }
    }
}

// Utility extension operator
operator fun String.invoke(): String = rootProject.ext[this] as String

fun calculateGitHash(): String {
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        "unknown"
    }
}

fun calculateGitBranch(): String {
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
            standardOutput = stdout
        }
        stdout.toString().trim()
    } catch (e: Exception) {
        "unknown"
    }
}

fun hasUnstaged(): Boolean {
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "status", "--porcelain")
            standardOutput = stdout
        }
        stdout.toString().trim().isNotEmpty()
    } catch (e: Exception) {
        false
    }
}

// Publishing configuration (for mod distribution platforms)
// Platform-specific publishing config is in forge/build.gradle.kts

// IDE configuration
apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")
