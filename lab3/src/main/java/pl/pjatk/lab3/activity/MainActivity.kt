package pl.pjatk.lab3.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pl.pjatk.lab3.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bmiButton: Button = findViewById(R.id.btn_activity_bmi)
        bmiButton.setOnClickListener { runActivity(BmiActivity::class.java) }
        val chartButton: Button = findViewById(R.id.btn_activity_chart)
        chartButton.setOnClickListener { runActivity(ChartActivity::class.java) }
        val quizButton: Button = findViewById(R.id.btn_activity_quiz)
        quizButton.setOnClickListener { runActivity(QuizActivity::class.java) }
    }

    private fun runActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}
