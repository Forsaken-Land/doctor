plugins {
    kotlin("jvm") version "1.4.32"
}

group = "top.limbang"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }
    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.slf4j:slf4j-api:1.7.30")
    }
}
