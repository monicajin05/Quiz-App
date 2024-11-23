package com.example.quizapp

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class TriviaApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var triviaApi: TriviaApi

    @Before
    fun setUp() {
        // Start MockWebServer
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Create a Retrofit instance pointing to the MockWebServer
        triviaApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // MockWebServer's URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getQuizQuestions returns correct data`() = runBlocking {
        // Define a sample JSON response
        val mockResponse = """
            {
                "response_code": 0,
                "results": [
                    {
                        "category": "General Knowledge",
                        "type": "multiple",
                        "difficulty": "medium",
                        "question": "What is the capital of France?",
                        "correct_answer": "Paris",
                        "incorrect_answers": ["London", "Berlin", "Madrid"]
                    }
                ]
            }
        """.trimIndent()

        // Enqueue the mock response
        mockWebServer.enqueue(MockResponse().setBody(mockResponse).setResponseCode(200))

        // Make the API call
        val response = triviaApi.getQuestions(amount = 1, category = 9, difficulty = "medium", type = "multiple")

        // Assertions
        assertEquals(1, response.results.size)
        assertEquals("What is the capital of France?", response.results[0].question)
        assertEquals("Paris", response.results[0].correct_answer)
        assertEquals(listOf("London", "Berlin", "Madrid"), response.results[0].incorrect_answers)
    }

    @Test
    fun `getQuizQuestions returns empty results on response code 0`() = runBlocking {
        // Define a mock response with empty results
        val mockResponse = """
            {
                "response_code": 0,
                "results": []
            }
        """.trimIndent()

        // Enqueue the mock response
        mockWebServer.enqueue(MockResponse().setBody(mockResponse).setResponseCode(200))

        // Make the API call
        val response = triviaApi.getQuestions(amount = 1, category = 9, difficulty = "medium", type = "multiple")

        // Assertions
        assertEquals(0, response.results.size)
    }
}