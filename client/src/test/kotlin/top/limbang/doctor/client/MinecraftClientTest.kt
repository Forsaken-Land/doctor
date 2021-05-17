package top.limbang.doctor.client


fun main() {
//    val host = "localhost"
//    val port = 25565
    val host = "mc.blackyin.xyz"
    val port = 524

//    val pingJson = MinecraftClient.ping(host, port).get()
//    println(AutoUtils.autoVersion(pingJson))
//    println(AutoUtils.autoForgeVersion(pingJson))


    MinecraftClient()
        .user("tfgv852@qq.com","12345678")
        .authServerUrl("https://skin.blackyin.xyz/api/yggdrasil/authserver")
        .sessionServerUrl("https://skin.blackyin.xyz/api/yggdrasil/sessionserver")
        .start(host,port)
}



