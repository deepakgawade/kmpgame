package org.example.kmpgame

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun daysUntilNewYear():Int{
    val today= Clock.System.todayIn(TimeZone.currentSystemDefault())

    val closetNewYear = LocalDate(today.year+1,1,1)

    return today.daysUntil(closetNewYear)
}

fun daysInPhrase():String = "There re only ${daysUntilNewYear()} days left until New Year! \uD83C\uDF86"