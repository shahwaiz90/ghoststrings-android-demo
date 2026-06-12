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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "GhostStringsDemo"
include(":app")

// 🔄 Composite Build: substitute maven sdk dependency with local sdk project
includeBuild("../GhostStrings-Android") {
    dependencySubstitution {
        substitute(module("ai.ghoststrings:android-sdk")).using(project(":ghoststrings-sdk"))
    }
}

