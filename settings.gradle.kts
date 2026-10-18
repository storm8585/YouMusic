pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "YouMusic"

include(":app")

include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:media")
include(":core:network")
include(":core:database")

include(":feature:home")
include(":feature:search")
include(":feature:player")
include(":feature:library")
