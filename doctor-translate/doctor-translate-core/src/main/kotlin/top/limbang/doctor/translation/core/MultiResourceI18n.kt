package top.limbang.doctor.translation.core

import top.limbang.doctor.translation.api.I18n
import top.limbang.doctor.translation.api.IResources

/**
 *
 * @author WarmthDawn
 * @since 2021-06-10
 */
class MultiResourceI18n : I18n {

    val resources: MutableList<IResources> = ArrayList()
    override fun translate(key: String): String {
        for (r in resources) {
            if (key in r) {
                return r[key]
            }
        }
        return key
    }

    fun add(type: Class<*>): Boolean {
        return try {
            val res = type.getConstructor().newInstance() as IResources
            res.load()
            resources.add(res)
        } catch (e: Exception) {
            false
        }
    }

}