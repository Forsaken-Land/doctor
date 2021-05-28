# doctor


doctor 一个简单的 `Minecraft` 库



| 模块         | 功能       | 备注 |
| ------------ | ---------- | ---- |
| client       | 客户端     |      |
| core         | 核心事件   |      |
| network      | 网络库     |      |
| protocol     | 协议库     |      |
| plugin-forge | forge 插件 |      |



# 用法

```kotlin
fun main(){
    // Ping服务器
    MinecraftClient.ping(host,port).get()
    
    // 离线登录
    MinecraftClient()
    	.name(name)
   		.start(host, port)
    
    // 正版/外置登录
    val client = MinecraftClient()
    	.user(username, password)
    	.authServerUrl(authServerUrl) // 正版登录无需设置
   		.sessionServerUrl(sessionServerUrl) // 正版登录无需设置
   		.start(host, port)
    
    // 断线重连
    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }
}
```

