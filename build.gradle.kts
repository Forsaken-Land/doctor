import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("maven-publish")
    id("application")
    id("signing")
}

allprojects {
    val projectVersion: String by project
    group = "top.fanua.doctor"
    version = projectVersion

    apply {
        plugin("org.gradle.maven-publish")
        plugin("application")
        plugin("signing")
    }

    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.fanua.top:8015/repository/maven-public/")
        maven("https://jitpack.io/")
        mavenCentral()
    }
}

subprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
        implementation("org.slf4j:slf4j-api:1.7.36")

        testImplementation(kotlin("test"))
        testImplementation(kotlin("test-junit"))
        testImplementation("ch.qos.logback:logback-classic:1.2.11")
    }

}

allprojects {
    java {
        withJavadocJar()
        withSourcesJar()
    }
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = project.name
                from(components["java"])
                pom {
                    url.set("https://github.com/Forsaken-Land/doctor")
                    name.set(project.name)
                    packaging = "jar"
                    description.set("doctor一个简单的Minecraft库")

                    scm {
                        url.set("https://github.com/Forsaken-Land/doctor")
                        connection.set("scm:git:git://github.com/Forsaken-Land/doctor")
                        developerConnection.set("scm:git:ssh://github.com/Forsaken-Land")
                    }
                    licenses {
                        license {
                            name.set("GNU General Public License v3.0")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        }
                    }
                    developers {
                        developer {
                            id.set("Doctor_Yin")
                            name.set("Doctor_Yin")
                            email.set("1921841421@qq.com")
                        }
                        developer {
                            id.set("limbang")
                            name.set("limbang")
                            email.set("495071565@qq.com")
                        }
                        developer {
                            id.set("WarmthDawn")
                            name.set("WarmthDawn")
                            email.set("1304793916@qq.com")
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
                url = uri(
                    if (project.version.toString()
                            .endsWith("-SNAPSHOT")
                    ) "https://s01.oss.sonatype.org/content/repositories/snapshots"
                    else "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                )
                val user: String by project
                val passwd: String by project
                credentials {
                    username = user
                    password = passwd
                }
            }
        }

//        publications {
//            create<MavenPublication>("maven") {
//                from(components["java"])
//                artifactId = project.name
//            }
//        }
//        repositories {
//            val user: String by project
//            val passwd: String by project
//            val mavenUrl: String by project
//            maven {
//                url = uri(mavenUrl)
//                credentials {
//                    username = user
//                    password = passwd
//                    isAllowInsecureProtocol = true
//                }
//            }
//        }
    }
    signing {
        val signingKeyId: String by project
        val signingKey: String by project
        val signingPassword: String by project
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
