package top.fanua.doctor.plugin.laggoggles.api

import top.fanua.doctor.plugin.forge.api.ModPacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:12:32
 */
interface LagPacket : ModPacket {
    override val channel: String
        get() = "LagGoggles"
}
