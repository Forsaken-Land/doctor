dependencies {
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    api(project(":doctor-network"))
    implementation(project(":doctor-plugin:doctor-plugin-forge-core"))

    testImplementation(project(":doctor-plugin:doctor-plugin-forge-laggoggles"))
    testImplementation(project(":doctor-plugin:doctor-plugin-forge-astralsorcery"))
    testImplementation(project(":doctor-plugin:doctor-plugin-forge-silentgear"))
    testImplementation(project(":doctor-translate:doctor-translate-all"))
}
