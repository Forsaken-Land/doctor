plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("maven-publish")
    id("application")
}

allprojects {
    val projectVersion: String by project
    group = "top.fanua.doctor"
    version = projectVersion

    apply {
        plugin("org.gradle.maven-publish")
        plugin("application")
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
            create<MavenPublication>("maven") {
                from(components["java"])
                artifactId = project.name
            }
        }
        repositories {
            val user: String by project
            val passwd: String by project
            val mavenUrl: String by project
            maven {
                url = uri(mavenUrl)
                credentials {
                    username = user
                    password = passwd
                    isAllowInsecureProtocol = true
                }
            }
        }
    }
}
