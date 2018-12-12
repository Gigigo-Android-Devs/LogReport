package com.gigigo.logger

import android.graphics.Color
import com.gigigo.logger.MessageType.DEBUG
import com.gigigo.logger.MessageType.ERROR
import com.gigigo.logger.MessageType.INFO
import com.gigigo.logger.MessageType.VERBOSE
import com.gigigo.logger.MessageType.WARNING

class LogMessage(val type: MessageType, val message: String)

enum class MessageType(index: Int) {
    VERBOSE(0),
    INFO(1),
    DEBUG(2),
    WARNING(3),
    ERROR(4)
}


fun String.messageType(): MessageType {
    return when {
        contains(" V/") -> VERBOSE
        contains(" I/") -> INFO
        contains(" D/") -> DEBUG
        contains(" W/") -> WARNING
        contains(" E/") -> ERROR
        else -> VERBOSE
    }
}

fun MessageType.color(): Int {
    return when(this) {
        VERBOSE -> Color.BLUE
        INFO -> Color.GREEN
        DEBUG -> Color.WHITE
        WARNING -> Color.YELLOW
        ERROR -> Color.RED
    }
}