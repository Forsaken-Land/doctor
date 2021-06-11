package top.limbang.doctor.plugin.laggoggles.api

import top.limbang.doctor.plugin.forge.api.ModPacket

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:12:32
 */
interface LagPacket : ModPacket {
    override val channel: String
        get() = "LagGoggles"
}
