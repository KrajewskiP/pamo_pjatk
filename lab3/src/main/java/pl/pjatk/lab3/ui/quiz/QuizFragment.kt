package pl.pjatk.lab3.ui.quiz

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.pjatk.lab3.R
import pl.pjatk.lab3.model.Question
import java.io.IOException

class QuizFragment : Fragment() {

    private lateinit var answerButtonsGrid: GridLayout
    private lateinit var startQuizBtn: Button
    private lateinit var quizContent: LinearLayout
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var endQuizBtn: Button
    private lateinit var startOverBtn: Button
    private lateinit var nextQuestionBtn: Button
    private lateinit var finishQuizBtn: Button
    private lateinit var exitBtn: Button
    private lateinit var questions: List<Question>
    private lateinit var questionHeader: TextView
    private lateinit var question: TextView
    private lateinit var actualQuestion: Question
    private lateinit var summaryContent: LinearLayout
    private lateinit var quizStartContent: LinearLayout
    private lateinit var summaryResult: TextView
    private lateinit var quizChoice: RadioGroup
    private var questionNumber = 0
    private var correctAnswers = 0
    private var answerButtonList: MutableList<Button> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_quiz, container, false)
        init(root)

        startQuizBtn.setOnClickListener { startQuiz() }
        nextQuestionBtn.setOnClickListener { nextQuestionBtnListener() }
        finishQuizBtn.setOnClickListener { finishQuizBtnListener() }
        endQuizBtn.setOnClickListener { finishQuizBtnListener() }
        startOverBtn.setOnClickListener { startOverBtnListener() }
        exitBtn.setOnClickListener { exitBtnListener() }

        return root
    }

    private fun exitBtnListener() {
        activity?.finish()
    }

    private fun startOverBtnListener() {
        val fragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.navigation_quiz_data) as QuizFragment
        requireActivity().supportFragmentManager.popBackStackImmediate()
        fragment.startQuiz()
    }

    private fun finishQuizBtnListener() {
        quizContent.visibility = GONE
        summaryContent.visibility = VISIBLE
        summaryResult.text = String.format(
            getString(R.string.quiz_summary_result),
            correctAnswers,
            questions.size
        )
    }

    private fun startQuiz() {
        questionNumber = 0
        correctAnswers = 0
        questions = loadQuestions().shuffled()

        loadQuestion()

        finishQuizBtn.isEnabled = false
        finishQuizBtn.visibility = GONE
        nextQuestionBtn.visibility = VISIBLE
        quizStartContent.visibility = GONE
        quizContent.visibility = VISIBLE
    }

    /**
     * init screen elements
     * @param root View
     */
    private fun init(root: View) {
        startQuizBtn = root.findViewById(R.id.btn_start_quiz)
        quizContent = root.findViewById(R.id.quiz_content)
        quizStartContent = root.findViewById(R.id.start_quiz_content)
        finishQuizBtn = root.findViewById(R.id.btn_finish_quiz)
        answerButtonsGrid = root.findViewById(R.id.answers_grid)
        questionHeader = root.findViewById(R.id.question_header)
        question = root.findViewById(R.id.question)
        button1 = root.findViewById(R.id.btn_answer_1)
        button2 = root.findViewById(R.id.btn_answer_2)
        button3 = root.findViewById(R.id.btn_answer_3)
        button4 = root.findViewById(R.id.btn_answer_4)
        answerButtonList.addAll(listOf(button1, button2, button3, button4))
        endQuizBtn = root.findViewById(R.id.btn_end_quiz)
        startOverBtn = root.findViewById(R.id.btn_start_over)
        exitBtn = root.findViewById(R.id.btn_exit_quiz)
        nextQuestionBtn = root.findViewById(R.id.btn_next_question)
        summaryContent = root.findViewById(R.id.quiz_summary_content)
        summaryResult = root.findViewById(R.id.quiz_summary_result)
        quizChoice = root.findViewById(R.id.quiz_choice)
    }

    /**
     * load question based on number
     * @param numberOfQuestion Int
     */
    private fun loadQuestion() {
        resetAnswers()
        questionHeader.text = String.format(
            getString(R.string.question_header_number),
            questionNumber + 1,
            questions.size
        )

        actualQuestion = questions[questionNumber]
        question.text = actualQuestion.question

        var noOfAnswer = 0
        val shuffledAnswers = actualQuestion.answers.shuffled()
        for (view in answerButtonsGrid.children) {
            view as Button
            view.text = shuffledAnswers[noOfAnswer].answer
            view.setOnClickListener {
                checkAnswer(view.text as String, view)
            }
            noOfAnswer++
        }
        if (questionNumber == questions.size - 1) {
            nextQuestionBtn.visibility = GONE
            finishQuizBtn.isEnabled = true
            finishQuizBtn.visibility = VISIBLE
        } else {
            questionNumber++
        }
    }

    /**
     * reset answers
     * unlocks buttons
     */
    private fun resetAnswers() {
        nextQuestionBtn.isEnabled = false
        answerButtonList.forEach {
            it.setBackgroundColor(ContextCompat.getColor(activity?.baseContext!!, R.color.blue))
            it.isEnabled = true
        }
    }

    /**
     * check if answer is correct
     * @param answer String
     */
    private fun checkAnswer(answer: String, button: Button) {
        answerButtonList.filter { b -> b != button }.forEach { it.isEnabled = false }
        var correct = false
        for (answerObj in actualQuestion.answers) {
            if (answer == answerObj.answer) {
                correct = answerObj.correct
                break
            }
        }
        val color = if (correct) {
            correctAnswers++
            Color.GREEN
        } else Color.RED
        button.setBackgroundColor(color)
        nextQuestionBtn.isEnabled = true
    }

    private fun nextQuestionBtnListener() {
        loadQuestion()
    }

    /**
     * load questions from json file
     * and convert to list of objects
     * @return List<Question>
     */
    private fun loadQuestions(): List<Question> {
        val checkedId = quizChoice.checkedRadioButtonId
        val choice: RadioButton = activity?.findViewById(checkedId)!!
        val fileName = if (choice.id == R.id.diet_quiz) {
            "quiz/diet.json"
        } else {
            "quiz/virus.json"
        }
        val gson = Gson()
        val jsonFileString = getJsonDataFromAsset(activity?.baseContext!!, fileName)
        val listPersonType = object : TypeToken<List<Question>>() {}.type

        return gson.fromJson(jsonFileString, listPersonType)
    }

    /**
     * load file from assets
     */
    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            Toast.makeText(
                activity,
                "Could not load questions, please try again",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(this.javaClass.name, "Error while reading JSON: ${ex.stackTrace}")
            return null
        }
        return jsonString
    }
}
