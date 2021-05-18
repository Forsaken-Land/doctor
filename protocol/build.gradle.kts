plugins {
    val kotlinVersion = "1.4.32"
    kotlin("plugin.serialization") version kotlinVersion
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("io.netty:netty-buffer:4.1.63.Final")
    implementation(project(":core"))
}
