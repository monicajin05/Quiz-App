package com.example.quizapp

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "App launched")

        setContent {
            QuizAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(android.graphics.Color.parseColor("#FAE1DF"))
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
    var showFeedback by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    if (showScore) {
        // Display Score Screen
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display Score
            Text(
                text = "Your Score: ${viewModel.score}/${viewModel.questions.size}",
                color = Color(android.graphics.Color.parseColor("#0D1F2D")),
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.resetQuiz()
                    showScore = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(android.graphics.Color.parseColor("#546A7B"))
                ),
                modifier = Modifier.padding(16.dp) // Add padding around the button
            ) {
                Text("Reset quiz")
            }
        }

    } else {
        if (viewModel.questions.isEmpty()) {
            // Fetch questions on first render
            viewModel.fetchQuestions()
            Text(text = "Loading questions...")
        } else {
            // Display current question
            val question = viewModel.questions[viewModel.currentQuestionIndex]
            val decodedQuestion = Html.fromHtml(question.question).toString()
            val options =
                (question.incorrect_answers + question.correct_answer.orEmpty()).map { Html.fromHtml(it).toString() }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = decodedQuestion, style = MaterialTheme.typography.headlineMedium,
                    color = Color(android.graphics.Color.parseColor("#0D1F2D")))
                Spacer(modifier = Modifier.height(16.dp))


                options.forEach { option ->
                    Button(onClick = {
                        selectedAnswer = option
                        val correct = viewModel.checkAnswer(option)

                        showFeedback = true
                    },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(Color(android.graphics.Color.parseColor("#546A7B")))
                    ) {
                        Text(text = option)
                    }
                }

                if (showFeedback) {
                    FeedbackFrame(feedback = viewModel.feedback, onDismiss = {
                        showFeedback= false
                        if (!viewModel.continueToNext()) {
                            showScore = true
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun FeedbackFrame(feedback: String, onDismiss: () -> Unit) {
    // Frame layout for feedback
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color(android.graphics.Color.parseColor("#9EA3B0")), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(
            text = feedback,
            color = Color(android.graphics.Color.parseColor("#0D1F2D")),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dismiss button to hide the feedback
        Button(onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(Color(android.graphics.Color.parseColor("#E4C3AD")))
        ) {
            Text(text = "Next Question",
                color = Color(android.graphics.Color.parseColor("#0D1F2D")))
        }
    }
}