plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
//    alias(libs.plugins.kotlinJvm)
}

// at least it somehow works, `.kts` is just a plain file
//  that requires also a lot of workarounds
apply(from = "./publish.gradle")

kotlin {
    androidTarget {
        // publishes also `debug` variant.. :'( but at least it publishes android at all
//        publishAllLibraryVariants()
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()

    applyDefaultHierarchyTemplate()

    cocoapods {
        summary = "Logging solution"
        homepage = "https://github.com/noties/Debug"
        version = project.version as String
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "KMPDebug"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // https://jeroenmols.com/blog/2021/03/17/share-code-kotlin-multiplatform/
        androidMain {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
        }

        // https://jeroenmols.com/blog/2021/03/17/share-code-kotlin-multiplatform/
        jvmMain {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

android {
    namespace = "io.noties.debug"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}