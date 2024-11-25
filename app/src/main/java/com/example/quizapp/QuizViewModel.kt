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

class QuizViewModel : ViewModel() {
    // set to mutableStateOf to make sure that when the value is updated, the UI will auto update to reflect it
    var questions by mutableStateOf<List<Question>>(emptyList())
    var currentQuestionIndex by mutableIntStateOf(0)
    var score by mutableIntStateOf(0)
    var feedback by mutableStateOf("")

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

    fun checkAnswer(answer: String): Boolean {
        // val correct = questions[currentQuestionIndex].correct_answer == answer
        val correctAnswer = questions[currentQuestionIndex].correct_answer
        val isCorrect = correctAnswer == answer

        feedback = if (isCorrect) {
            score++
            "Correct!"
        } else {
            "Incorrect. The correct answer is $correctAnswer."
        }
        return isCorrect
    }

    fun continueToNext(): Boolean {
        /* if the questions have not run out yet, continue */
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            return true
        }
        return false
    }
}