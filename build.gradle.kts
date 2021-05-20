plugins {
    val kotlinVersion = "1.4.32"
    kotlin("jvm") version kotlinVersion
    `maven-publish`
}

group = "top.limbang"
val projectVersion: String by project
version = projectVersion

repositories {
    val CI_JOB_TOKEN: String by project
    maven {
        url = uri("https://git.blackyin.xyz:8443/api/v4/groups/10/-/packages/maven")
        name = "GitLab"
        credentials(HttpHeaderCredentials::class.java) {
            name = "Job-Token"
            value = System.getenv(CI_JOB_TOKEN)
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
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
//publishing {
//    val gitLabPrivateToken: String by project
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
//        }
//    }
//    repositories {
//        maven(url = "https://git.blackyin.xyz:8443/api/v4/projects/30/packages/maven") {
//            credentials(HttpHeaderCredentials::class.java) {
//                name = "Private-Token"
//                value = gitLabPrivateToken
//            }
//            authentication {
//                create<HttpHeaderAuthentication>("header")
//            }
//        }
//    }
//}

