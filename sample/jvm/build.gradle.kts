plugins {
    java
    application
    alias(libs.plugins.kotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

afterEvaluate {
    tasks.withType(JavaExec::class.java).configureEach {
        if (name.endsWith("main()")) {
            notCompatibleWithConfigurationCache("JavaExec created by IntelliJ")
        }
    }
}

dependencies {
    implementation(project(":debug"))
}

application {
    mainClass = "io.noties.debug.sample.jvm.MainKt"
}

