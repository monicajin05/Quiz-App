package com.example.quizapp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The Api interface, which outlines the general properties of the quiz
 */
interface TriviaApi {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int? = null, // null value handling
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null
    ): TriviaResponse

}

/**
 * Defines the response and stores the results in a list
 */
data class TriviaResponse(val results: List<Question>)

/**
 * Defines the properties of a question
 */
data class Question(
    /** the String question */
    val question: String,
    /** the correct answer */
    val correct_answer: String,
    /** the list of incorrect answers */
    val incorrect_answers: List<String>
)

/**
 * The object Api fetches from the website and creates a new instance
 * of class TriviaApi
 */
object Api {
    private const val BASE_URL = "https://opentdb.com/"

    val triviaApi: TriviaApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TriviaApi::class.java)
}