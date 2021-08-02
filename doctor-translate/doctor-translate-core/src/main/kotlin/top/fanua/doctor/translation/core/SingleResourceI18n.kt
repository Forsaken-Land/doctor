package top.fanua.doctor.translation.core

import top.fanua.doctor.translation.api.I18n
import top.fanua.doctor.translation.api.IResources

/**
 *
 * @author WarmthDawn
 * @since 2021-06-10
 */
class SingleResourceI18n(
    val resource: IResources
) : I18n {

    override fun translate(key: String): String {
        return if (key in resource) resource[key] else key
    }

    companion object {
        fun create(type: Class<*>): SingleResourceI18n {
            val res = type.getConstructor().newInstance() as IResources
            res.load()
            return SingleResourceI18n(res)
        }
    }
}
