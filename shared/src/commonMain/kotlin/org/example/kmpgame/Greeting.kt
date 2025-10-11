package org.example.kmpgame

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class Greeting {
    private val platform = getPlatform()
    private val rocketComponent = RocketComponent()


    @NativeCoroutines
    fun greet(): Flow<String> = flow {
        emit(if (Random.nextBoolean()) "Hi!" else "Hello!")
        delay(1.seconds)
        emit("Guess what this is! > ${platform.name.reversed()}")
        delay(1.seconds)
        emit(daysInPhrase())
        emit(rocketComponent.launchPhrase())
        emit(rocketComponent.launchUsersPhrase())
    }

    @NativeCoroutines
    suspend fun createUser(name: String, email:String):User?{
        val user=User(name = name,email=email)
        return rocketComponent.createUsers(user)
    }
}