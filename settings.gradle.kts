pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.quiltmc.org/repository/release") }
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
include("forge")

rootProject.name = "Railway"
