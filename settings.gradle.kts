pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.quiltmc.org/repository/release") }
        gradlePluginPortal()
    }

    plugins {
        operator fun String.invoke() = extra.properties[this] as String
        id("architectury-plugin") version "architectury_plugin_version"() apply false
        id("dev.architectury.loom") version "loom_version"() apply false
        id("me.modmuss50.mod-publish-plugin") version "mod_publish_version"() apply false
        id("com.gradleup.shadow") version "shadow_version"() apply false
        id("dev.ithundxr.silk") version "silk_version"() apply false
        id("net.kyori.blossom") version "blossom_version"() apply false
        id("org.jetbrains.gradle.plugin.idea-ext") version "idea_ext_version"() apply false
    }
}

include("common")
include("fabric")
include("forge")

rootProject.name = "Railway"

println("Steam 'n' Rails v${extra.properties["mod_version"]}")
