// taken from : https://github.com/AAkira/Napier

apply(plugin = "maven-publish")
apply(plugin = "signing")

fun Project.publishing(action: PublishingExtension.() -> Unit) =
    configure(action)

fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
    configure(configure)

val publications: PublicationContainer =
    (extensions.getByName("publishing") as PublishingExtension).publications

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

operator fun Project.get(name: String): String? {
    return if (this.hasProperty(name)) this.property(name) as? String else null
}

val POM_NAME = project["POM_NAME"]
val POM_DESCRIPTION = project["POM_DESCRIPTION"]
val POM_URL = project["POM_URL"]
val POM_LICENCE_NAME = project["POM_LICENCE_NAME"]
val POM_LICENCE_URL = project["POM_LICENCE_URL"]
val POM_LICENCE_DIST = project["POM_LICENCE_DIST"]
val POM_DEVELOPER_ID = project["POM_DEVELOPER_ID"]
val POM_DEVELOPER_NAME = project["POM_DEVELOPER_NAME"]
val POM_SCM_URL = project["POM_SCM_URL"]

publishing {
    publications.all {
        group = project.group
        version = project.version
    }

    publications.withType<MavenPublication>().all {
        artifact(javadocJar.get())

        pom {
            name.set(POM_NAME)
            description.set(POM_DESCRIPTION)
            url.set(POM_URL)
            licenses {
                license {
                    name.set(POM_LICENCE_NAME)
                    url.set(POM_LICENCE_URL)
                    distribution.set(POM_LICENCE_DIST)
                }
            }
            developers {
                developer {
                    id.set(POM_DEVELOPER_ID)
                    name.set(POM_DEVELOPER_NAME)
                }
            }
            scm {
                url.set(POM_SCM_URL)
            }
        }
    }

    // FIXME - workaround for https://github.com/gradle/gradle/issues/26091
    val signingTasks = tasks.withType<Sign>()
    tasks.withType<AbstractPublishToMaven>().configureEach {
        mustRunAfter(signingTasks)
    }

    repositories {
        maven {
            name = "Sonatype"
            url = uri(
                if (project.version.toString().endsWith("SNAPSHOT")) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                }
            )

            credentials {
                username = NEXUS_USERNAME
                password = NEXUS_PASSWORD
            }
        }
//        maven {
//            url = uri("$rootDir/repo")
//        }
    }
}

signing {
    sign(publications)
}
