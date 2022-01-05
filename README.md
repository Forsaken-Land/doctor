# doctor

doctor 一个简单的 `Minecraft` 库

| 模块                            | 功能       | 备注                |
| ------------------------------ | ---------  | ------------------ |
| client                         | 客户端      |                    |
| core                           | 核心事件    |                     |
| network                        | 网络库      |                    |
| protocol                       | 协议库      |                    |
| plugin-forge                   | forge 插件  |                    |
| plugin-forge-laggoggles        | Lag 插件    |只有解析包(需要补全)   |
| plugin-forge-allLoginPlugin    | 高版本登录插件|FML2的专属功能       |
| plugin-forge-ftbquests         | FTB-Quests | FTB任务插件         |
# 用法

```kotlin
fun main() {
    // Ping服务器
    MinecraftClient.ping(host, port).get()


    // 创建一个客户端
    val client = MinecraftClient.builder()
        //.name(name)   //设置后是离线登录
        .user(username, password) //name和user必选一个
        .authServerUrl(authServerUrl)  //不设置是正版登录
        .sessionServerUrl(sessionServerUrl) //不设置为正版登录
        .plugin(PlayerPlugin())  //玩家在线人数插件
        .plugin(AutoVersionForgePlugin()) //自动判断Forge登录插件
        .plugin(TabCompletePlugin()) //命令补全插件
        .plugin(TpsPlugin()) // Tps插件(暂时只支持1.12.2
        .enableAllLoginPlugin() //开启FML2支持功能
        .build()

    //连接服务器
    client.start(host, port) //设置host和port,启动连接

    // 断线重连
    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }
}
```

## 导入依赖

#### build.gradle

```groovy
repositories {
    maven {
        url 'https://maven.fanua.top:8015/repository/maven-public'
    }
}
dependencies {
    def doctorVersion = "1.3.4-dev-6"
    implementation "top.fanua.doctor:doctor-all:$doctorVersion"
}


```

#### build.gradle.kts

```kotlin

repositories {
    maven("https://maven.fanua.top:8015/repository/maven-public")
}
dependencies {
    val doctorVersion = "1.3.4-dev-6"
    implementation("top.fanua.doctor:doctor-all:$doctorVersion")
}

```

