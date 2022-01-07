dependencies {
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.github.Querz:NBT:6.1")
    api(project(":doctor-network"))
    implementation(project(":doctor-plugin:doctor-plugin-forge-core"))

    testImplementation(project(":doctor-plugin:doctor-plugin-forge-laggoggles"))
    testImplementation(project(":doctor-plugin:doctor-plugin-forge-ftbquests"))
    testImplementation(project(":doctor-plugin:doctor-plugin-forge-fix"))
    testImplementation(project(":doctor-plugin:doctor-plugin-forge-allLoginPlugin"))
    testImplementation(project(":doctor-translate:doctor-translate-all"))


}
