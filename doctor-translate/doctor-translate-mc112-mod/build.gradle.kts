dependencies {
    api(project(":doctor-translate:doctor-translate-mc112"))
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    doFirst {
        generateList()
    }
}

fun generateList() {
    println("Task :generateList")
    val path = "src/main/resources/mc112modlang/"
    val pathFile = project.layout.projectDirectory.file(path).asFile
    val file = project.layout.projectDirectory.file("$path/translateList").asFile
    var list = ""
    pathFile.walk().maxDepth(3)
        .filter { it.isFile }
        .filter { it.name != "translateList" }
        .forEach {
            list += "${it.canonicalPath.substringAfter("mc112modlang")}\n"
        }
    file.writeText(list.substringBeforeLast("\n"))
}