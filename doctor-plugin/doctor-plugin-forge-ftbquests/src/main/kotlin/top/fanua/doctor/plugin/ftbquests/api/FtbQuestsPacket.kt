package top.fanua.doctor.plugin.ftbquests.api

import top.fanua.doctor.plugin.forge.api.ModPacket

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:13:32
 */
interface FtbQuestsPacket : ModPacket {
    override val channel: String
        get() = "ftbquests"
}
