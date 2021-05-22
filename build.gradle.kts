plugins {
    val kotlinVersion = "1.4.32"
    kotlin("jvm") version kotlinVersion
//    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version kotlinVersion
    `maven-publish`
    id("application")
}



dependencies {
    //doctor-all包
    api(project(":client"))
    api(project(":network"))
    api(project(":core"))
    api(project(":protocol"))
    api(project(":plugin-forge"))
}

allprojects {
    group = "top.limbang"
    val projectVersion: String by project
    version = projectVersion


    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.serialization")
        plugin("org.gradle.maven-publish")
        plugin("application")
    }




    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        maven("http://web.blackyin.top:8015/repository/maven-public/") {
            isAllowInsecureProtocol = true
        }
        maven("https://jitpack.io/")
        mavenCentral()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifactId = if (project.rootProject == project) {
                    "${project.name}-all"  //根项目，名字打包为doctor-all
                } else {
                    "${project.rootProject.name}-${project.name}" //其他项目
                }
            }
        }
        repositories {
            val user: String by project
            val passwd: String by project
            maven {
                url = uri("http://web.blackyin.top:8015/repository/maven-releases")
                credentials {
                    username = user
                    password = passwd
                    isAllowInsecureProtocol = true
                }
            }
        }
    }
}


subprojects {

    dependencies {
        val kotlinVersion = "1.4.32"
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
        implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        implementation("org.slf4j:slf4j-api:1.7.30")
        implementation("com.google.guava:guava:30.1.1-jre")
        implementation("com.google.code.gson:gson:2.8.6")

        testImplementation("junit:junit:4.12")
        testImplementation("ch.qos.logback:logback-classic:1.2.3")
    }


}

