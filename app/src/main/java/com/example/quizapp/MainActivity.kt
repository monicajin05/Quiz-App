package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizScreen()
                }
            }
        }
    }
}

@Composable
fun QuizScreen(viewModel: QuizViewModel = viewModel()) {
    var showScore by remember { mutableStateOf(false) }

    if (showScore) {
        // Display Score Screen
        Text(text = "Your Score: ${viewModel.score}/${viewModel.questions.size}")
    } else {
        if (viewModel.questions.isEmpty()) {
            // Fetch questions on first render
            viewModel.fetchQuestions()
            Text(text = "Loading questions...")
        } else {
            // Display current question
            val question = viewModel.questions[viewModel.currentQuestionIndex]
            val options = (question.incorrect_answers + question.correct_answer.orEmpty()).shuffled()

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = question.question, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                options.forEach { option ->
                    Button(onClick = {
                        if (viewModel.checkAnswer(option)) {
                            if (!viewModel.continueToNext()) {
                                showScore = true
                            }
                        }
                    }) {
                        Text(text = option)
                    }
                }
            }
        }
    }
}