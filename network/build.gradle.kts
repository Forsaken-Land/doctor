repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":protocol"))
    implementation(project(":core"))
    api("io.netty:netty-all:4.1.56.Final")
}
