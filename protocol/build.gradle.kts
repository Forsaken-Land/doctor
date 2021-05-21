plugins {
    val kotlinVersion = "1.4.32"
    kotlin("plugin.serialization") version kotlinVersion
}

repositories {
    maven("https://papermc.io/repo/repository/maven-releases/")
    maven("https://jitpack.io/")
    mavenCentral()
}


dependencies {
    implementation("io.netty:netty-buffer:4.1.63.Final")
    implementation("com.github.Querz:NBT:6.1")
    implementation(project(":core"))
}
