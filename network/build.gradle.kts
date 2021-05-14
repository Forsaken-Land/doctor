repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":protocol"))
    implementation(project(":core"))
    implementation("io.netty:netty-all:4.1.56.Final")
}
