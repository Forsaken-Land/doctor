plugins {
    val kotlinVersion = "1.4.32"
    kotlin("jvm") version kotlinVersion
    `maven-publish`
}

group = "top.limbang"
val projectVersion: String by project
version = projectVersion

repositories {
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }


    dependencies {
        val kotlinVersion = "1.4.32"
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
        implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
        compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        implementation("org.slf4j:slf4j-api:1.7.30")
        implementation("com.google.guava:guava:30.1.1-jre")
        implementation("com.google.guava:guava:30.1.1-jre")
        implementation("com.google.code.gson:gson:2.8.6")

        testImplementation("junit:junit:4.12")
        testImplementation("ch.qos.logback:logback-classic:1.2.3")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        val user: String by project
        val passwd: String by project
        maven {
            url = uri("http://web.blackyin.top:8015/repository/maven-releases/")
            credentials {
                username = user
                password = passwd
            }
        }
    }
}

