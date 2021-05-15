package top.limbang.doctor.client

import top.limbang.doctor.client.listener.HandshakeListener
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.network.core.DefaultClientCodecInitializer
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.handler.ReadPacketListener

/**
 * ### Minecraft 客户端
 */
class MinecraftClient(){

    fun start(host:String,port:Int){

    }

    suspend fun ping(host:String, port:Int){

        val defaultEventEmitter = DefaultEventEmitter()
        defaultEventEmitter.addListener(HandshakeListener())
        defaultEventEmitter.addListener(ReadPacketListener())

        var networkManager = NetworkManager.Builder()
            .host(host)
            .port(port)
            .eventEmitter(defaultEventEmitter)
            .build()

        networkManager.preInit(DefaultClientCodecInitializer())
        networkManager.connect()
    }
}