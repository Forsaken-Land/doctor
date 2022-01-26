# doctor

doctor 一个简单的 `Minecraft` 库

| 模块                      | 功能         | 备注                  |
|-------------------------|------------|---------------------|
| client                  | 客户端        |                     |
| core                    | 核心事件       |                     |
| network                 | 网络库        |                     |
| protocol                | 协议库        |                     |
| plugin-forge            | forge 插件   |                     |
| plugin-forge-laggoggles | Lag 插件     | 只有解析包(需要补全)         |
| plugin-forge-fix        | 修复协议       | 修复数据溢出<br/>修复fml2登录 |
| plugin-forge-ftbquests  | FTB-Quests | FTB任务插件             |

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
        .plugin(PlayerWorldPlugin()) //世界插件
        .plugin(PlayerListPlugin()) //玩家列表插件
        .plugin(AutoVersionForgePlugin()) //自动判断Forge登录插件
        .plugin(TabCompletePlugin()) //命令补全插件
        .plugin(TpsPlugin()) // Tps插件
        .plugin(PluginFtbQuests()) //FTB任务插件(仅支持手动
        .plugin(PlayerStatusPlugin()) //玩家状态插件
        .plugin(PlayerBagPlugin()) //玩家背包插件
        .plugin(PluginFix()) //mod修复插件(fml2登录需开启 (需要玩家背包插件和玩家状态插件
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
    def doctorVersion = "1.3.7"
    implementation "top.fanua.doctor:doctor-all:$doctorVersion"
}


```

#### build.gradle.kts

```kotlin

repositories {
    maven("https://maven.fanua.top:8015/repository/maven-public")
}
dependencies {
    val doctorVersion = "1.3.7"
    implementation("top.fanua.doctor:doctor-all:$doctorVersion")
}

```

