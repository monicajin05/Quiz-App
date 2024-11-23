package com.example.quizapp
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    var questions: List<Question> = emptyList()
    var currentQuestionIndex = 0
    var score = 0

    fun fetchQuestions(amount: Int = 10, category: Int = 9, difficulty: String = "medium") {
        /* launches coroutine tied to the lifecycle of a ViewModel, starts a new coroutine*/
        viewModelScope.launch {
            try {
                val response = Api.triviaApi.getQuestions(amount, category, difficulty, "multiple")
                questions = response.results
                currentQuestionIndex = 0
                score = 0
            } catch (e : Exception) {
                Log.e("QuizViewModel", "Error fetching questions: ${e.message}")
            }
        }
    }

    fun checkAnswer(answer: String): Boolean {
        val correct = questions[currentQuestionIndex].correct_answer == answer
        if (correct) score++
        return correct
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