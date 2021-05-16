package top.limbang.doctor.client

import top.limbang.doctor.client.listener.HandshakeListener
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.network.core.DefaultClientCodecInitializer
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.handler.ReadPacketListener
import top.limbang.doctor.protocol.version.autoversion.PingProtocol

/**
 * ### Minecraft 客户端
 */
class MinecraftClient() : EventEmitter by DefaultEventEmitter(){

    fun start(host:String,port:Int){

    }

    fun ping(host:String, port:Int){

        val defaultEventEmitter = DefaultEventEmitter()
        defaultEventEmitter.addListener(HandshakeListener())
//        defaultEventEmitter.addListener(ReadPacketListener())

        val networkManager = NetworkManager.Builder()
            .host(host)
            .port(port)
            .eventEmitter(this)
            .protocol(PingProtocol())
            .build()
        networkManager.connect()
    }
}