package com.example.quizapp
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Class that manages components to be stored
 */
class QuizViewModel : ViewModel() {
    // set to mutableStateOf to make sure that when the value is updated, the UI will auto update to reflect it
    /** the list of questions */
    var questions by mutableStateOf<List<Question>>(emptyList())
    /** the current question */
    var currentQuestionIndex by mutableIntStateOf(0)
    /** the current score */
    var score by mutableIntStateOf(0)
    /** the feedback for the user */
    var feedback by mutableStateOf("")

    /**
     * fetches questions from the api
     * @param amount the amount of questions to fetch (default 10)
     * @param category the category of question (default 9)
     * @param difficulty the difficulty of the questions (default medium)
     */
    fun fetchQuestions(amount: Int = 10, category: Int = 9, difficulty: String = "medium") {
        /* launches coroutine tied to the lifecycle of a ViewModel, starts a new coroutine*/
        viewModelScope.launch {
            try {
                val response = Api.triviaApi.getQuestions(amount, category, difficulty, "multiple")
                questions = response.results
                currentQuestionIndex = 0
                score = 0
            } catch (e : IOException) {
                Log.e("QuizViewModel", "Error fetching questions: ${e.message}")
            }
        }
    }

    /**
     * Checks whether the answer is correct
     * @param answer the user answer to the question
     * @return true if the answer is correct
     */
    fun checkAnswer(answer: String): Boolean {
        // val correct = questions[currentQuestionIndex].correct_answer == answer
        val correctAnswer = questions[currentQuestionIndex].correct_answer
        val isCorrect = correctAnswer == answer

        feedback = if (isCorrect) {
            // increment score if correct
            score++
            "Correct!"
        } else {
            "Incorrect. The correct answer is $correctAnswer."
        }
        return isCorrect
    }

    /**
     * Moves to the next question once the user answers
     * @return true if the question index is incremented
     */
    fun continueToNext(): Boolean {
        /* if the questions have not run out yet, continue */
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            return true
        }
        return false
    }

    /**
     * resets the quiz once the user is completed
     * all params to default
     */
    fun resetQuiz() {
        currentQuestionIndex = 0
        score = 0
        feedback = ""
    }
}