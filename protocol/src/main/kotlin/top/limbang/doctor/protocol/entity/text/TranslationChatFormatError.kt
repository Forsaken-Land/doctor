package top.limbang.doctor.protocol.entity.text

import top.limbang.doctor.protocol.entity.text.TranslationChat

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
class TranslationChatFormatError : IllegalArgumentException {
    constructor(
        component: TranslationChat,
        message: String
    ) : super(java.lang.String.format("Error parsing: %s: %s", component, message)) {
    }

    constructor(
        component: TranslationChat,
        index: Int
    ) : super(java.lang.String.format("Invalid index %d requested for %s", index, component)) {
    }

    constructor(
        component: TranslationChat,
        cause: Throwable
    ) : super(java.lang.String.format("Error while parsing: %s", component), cause) {
    }
}
