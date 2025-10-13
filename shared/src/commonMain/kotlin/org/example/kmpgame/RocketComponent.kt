package org.example.kmpgame

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class RocketComponent {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun getlastDateOfSuccessfulMission(): String {
        val rockets: List<RocketLaunch> =
            httpClient.get("https://api.spacexdata.com/v4/launches").body()
        val lastSuccessLaunch = rockets.last { it.launchSuccess == true }
        val date = Instant.parse(lastSuccessLaunch.launchDateUTC)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return " ${date.month} ${date.day}, ${date.year}"
    }

    suspend fun launchPhrase(): String =
        try {
            "The last successful launch was on ${getlastDateOfSuccessfulMission()} 🚀"
        } catch (e: Exception) {
            println("Exception during getting the date of the last successful launch $e")
            "Error occurred"
        }

    private suspend fun getAllUsers(): List<User> {
        val users: List<User> = try {
            //httpClient.get("http://10.0.2.2/users").body()
            httpClient.get("http://192.168.1.103/users").body()
        } catch (e: Exception) {
            println("Exception during getting the date of the last successful launch $e")
            emptyList()
        }
        return users
    }
     suspend fun createUsers(user:User): User? {
        return  try {
//            httpClient.post("http://10.0.2.2/users"){
//                contentType(ContentType.Application.Json)
//                setBody(user)
//            }.body()
            httpClient.post("http://192.168.1.103/users"){
                contentType(ContentType.Application.Json)
                setBody(user)
            }.body()
        } catch (e: Exception) {
            log("MainViewmodel","In RocketComponent $e")

            println("Failed to create user $e")
       null }
    }

    suspend fun launchUsersPhrase(): String =
        try {
            var userStr: String = ""

            val users = getAllUsers()

            users.forEach { user -> userStr = userStr + "${user.name}:${user.email}," }
            userStr
        } catch (e: Exception) {
            println("Exception during getting the date of the last successful launch $e")
            "Error occurred"
        }
}