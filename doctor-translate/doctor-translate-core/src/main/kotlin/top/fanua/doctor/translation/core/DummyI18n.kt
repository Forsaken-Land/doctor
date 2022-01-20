package top.fanua.doctor.translation.core

import top.fanua.doctor.translation.api.I18n

/**
 * 空白的翻译，啥都不干
 * @author WarmthDawn
 * @since 2021-06-09
 */
object DummyI18n : I18n {
    override fun translate(key: String): String {
        return key
    }

    override fun translateItem(start: String): Map<String, String> {
        return emptyMap()
    }
}
