package org.example.kmpgame

import platform.Foundation.NSLog

actual fun log(tag: String, message: String) {
    NSLog("%s: %s", tag, message)
}
