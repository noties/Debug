plugins {
    java
    application
    alias(libs.plugins.kotlinJvm)
}

application {
    mainClass = "io.noties.debug.sample.jvm.MainKt"
}

dependencies {
    implementation(project(":shared"))
}