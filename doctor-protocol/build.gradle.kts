dependencies {
    implementation("io.netty:netty-buffer:4.1.63.Final")
    implementation("com.github.Querz:NBT:6.1")

    api(project(":doctor-core"))
    api(project(":doctor-translate:doctor-translate-core"))
}
