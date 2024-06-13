enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// it seems Android Studio and gradle go crazy if project is named `Debug`
// 29, method getDebug() is already defined in class org.gradle.accessors.dm.RootProjectAccessor
rootProject.name = "KMPDebug"
include(":debug")

include(":sample:android")
include(":sample:jvm")
