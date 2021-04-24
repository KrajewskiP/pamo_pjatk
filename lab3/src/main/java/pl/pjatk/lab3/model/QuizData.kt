package pl.pjatk.lab3.model

data class Question(
    val question: String,
    val answers: List<Answer>
)

data class Answer(
    val answer: String,
    val correct: Boolean
)
