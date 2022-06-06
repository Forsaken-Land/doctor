dependencies {
    implementation("io.netty:netty-all:4.1.77.Final")
    implementation("com.github.Querz:NBT:6.1")

    api(project(":doctor-core"))
    api(project(":doctor-translate:doctor-translate-core"))
}
