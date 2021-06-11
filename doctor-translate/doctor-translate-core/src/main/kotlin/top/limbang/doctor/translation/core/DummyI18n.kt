package top.limbang.doctor.translation.core

import top.limbang.doctor.translation.api.I18n

/**
 * 空白的翻译，啥都不干
 * @author WarmthDawn
 * @since 2021-06-09
 */
object DummyI18n : I18n {
    override fun translate(key: String): String {
        return key
    }
}