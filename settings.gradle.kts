rootProject.name = "MagicSpellsParent"

include("core")
include("factions")
include("memory")
include("shop")
include("teams")
include("towny")

startParameter.isParallelProjectExecutionEnabled = true

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

