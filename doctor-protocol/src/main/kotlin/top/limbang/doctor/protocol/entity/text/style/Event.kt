package top.limbang.doctor.protocol.entity.text.style

import top.limbang.doctor.protocol.entity.text.IChat

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
data class ClickEvent(val action: ClickAction, val value: String) {

}

data class HoverEvent(val action: HoverAction, val value: IChat) {

}

enum class ClickAction {
    OPEN_URL,
    OPEN_FILE,
    RUN_COMMAND,
    SUGGEST_COMMAND,
    CHANGE_PAGE;

    companion object {
        val ACTIONS = ClickAction.values().associateBy { it.name.toLowerCase() }
        fun getAction(name: String?): ClickAction? =
            if (name == null) null else ACTIONS[name]
    }

}

enum class HoverAction {
    SHOW_TEXT,
    SHOW_ITEM,
    SHOW_ENTITY;

    companion object {
        val ACTIONS = HoverAction.values().associateBy { it.name.toLowerCase() }
        fun getAction(name: String?): HoverAction? =
            if (name == null) null else ACTIONS[name]
    }

}