package org.example.kmpgame

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform