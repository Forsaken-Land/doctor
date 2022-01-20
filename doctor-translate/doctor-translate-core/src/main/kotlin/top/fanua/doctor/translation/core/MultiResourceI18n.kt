package top.fanua.doctor.translation.core

import top.fanua.doctor.translation.api.I18n
import top.fanua.doctor.translation.api.IResources

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

    override fun translateItem(start: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (r in resources) {
            if (r.startWith(start)) {
                r.getList(start).forEach { (t, u) -> map[t] = u }
            }
        }
        if (map.isEmpty()) map[start] = start
        return map
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
