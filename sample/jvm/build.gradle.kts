plugins {
    java
    application
    alias(libs.plugins.kotlinJvm)
}
dependencies {
    implementation(project(":debug"))
}

application {
    mainClass = "io.noties.debug.sample.jvm.MainKt"
}

