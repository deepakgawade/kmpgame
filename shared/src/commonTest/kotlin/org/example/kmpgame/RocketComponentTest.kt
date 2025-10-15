package org.example.kmpgame

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals


class RocketComponentTest{
    @Test
    fun  testLaunchUserPhase_returnCorrectlyFormattedString() = runTest{
        val mockEngine = MockEngine{
            request ->
            if(request.url.toString().endsWith("/users")){
                respond(content= """[{"Name":"Deepak", "Email":"a@b.com"}]""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType,"application/json")
                )
            }else{
                respond("Not Found", HttpStatusCode.NotFound)
            }
        }

        val mockClient = HttpClient(mockEngine){
            install(ContentNegotiation){json()}
        }

        val rocketComponent = RocketComponent(mockClient)

        // 2. Act: Call the function we want to test
        val result = rocketComponent.getAllUsers()

        // 3. Assert: Check if the result is what we expect
        val expectedUsers = listOf(User(name = "Deepak", email = "a@b.com"))
        assertEquals(expectedUsers, result)

    }
}