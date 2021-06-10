dependencies {
    implementation(project(":network"))
    implementation(project(":core"))
    implementation(project(":protocol"))
    implementation(project(":plugin-forge"))
    implementation(project(":plugin-forge-laggoggles"))
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    testImplementation(project(":translate:mc112-vanilla"))
    testImplementation(project(":translate:mc112-mod"))

//    implementation(project(":translate:translate-core"))
//    testImplementation(project(":translate:mc116-vanilla"))

}
