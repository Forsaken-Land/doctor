package top.limbang.doctor.protocol.entity.text.translation

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
object I18n {
    fun translate(key: String): String {
        return Resources.rawMessage(key)
    }

    val Resources: LangResources = LangResources().also { it.loadLocaleData() }
}