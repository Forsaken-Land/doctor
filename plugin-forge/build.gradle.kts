plugins {
    val kotlinVersion = "1.4.32"
    kotlin("plugin.serialization") version kotlinVersion
}
repositories {
    mavenCentral()
}
dependencies {
    implementation(project(":network"))
    implementation(project(":core"))
    implementation(project(":protocol"))
    implementation("io.netty:netty-all:4.1.56.Final")
}
