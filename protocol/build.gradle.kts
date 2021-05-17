plugins {
    val kotlinVersion = "1.4.32"
    kotlin("plugin.serialization") version kotlinVersion
}

repositories {
    maven("https://papermc.io/repo/repository/maven-releases/")
    mavenCentral()
}


dependencies {
    implementation("io.netty:netty-buffer:4.1.63.Final")
    implementation("com.mojang:authlib:1.5.25")
    implementation(project(":core"))
}
