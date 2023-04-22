pluginManagement {
    repositories {
        google()
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

rootProject.name = "Tinkoff Cup"
include(":demoapp")

include(":core")
include(":core:utils")
include(":core:design")
include(":core:designsystem")
include(":core:mvvm")
include(":core:ext")
include(":core:network")
