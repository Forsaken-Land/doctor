dependencies {
    api(project(":doctor-translate:doctor-translate-mc112"))
}

tasks.register("generateList") {
    dependsOn(
        generateList("src/main/resources/mc112modlang/")
    )
}

fun generateList(path: String) {
    val pathFile = project.layout.projectDirectory.file("$path").asFile
    val file = project.layout.projectDirectory.file("$path/translateList").asFile
    var list = ""
    pathFile.walk().maxDepth(3)
        .filter { it.isFile }
        .filter { it.name != "translateList" }
        .forEach {
            list += "${it.absolutePath.substringAfter(path)}\n"
        }
    file.writeText(list.substringBeforeLast("\n"))
}