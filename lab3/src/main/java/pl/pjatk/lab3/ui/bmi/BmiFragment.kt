package pl.pjatk.lab3.ui.bmi

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.pjatk.lab3.R
import pl.pjatk.lab3.util.isValid
import java.util.*
import kotlin.math.pow

class BmiFragment : Fragment() {

    private lateinit var heightInput: TextView
    private lateinit var weightInput: TextView
    private lateinit var resultComponent: LinearLayout
    private lateinit var result: TextView
    private lateinit var bar: ProgressBar
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_bmi, container, false)
        val calculate: Button = root.findViewById(R.id.calculate)
        val reset: Button = root.findViewById(R.id.reset)

        heightInput = root.findViewById(R.id.inputHeight)
        weightInput = root.findViewById(R.id.inputWeight)
        resultComponent = root.findViewById(R.id.resultComponent)
        result = root.findViewById(R.id.result)
        bar = root.findViewById(R.id.progressBar)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return root

        heightInput.text = sharedPref.getString(getString(R.string.height), null)
        weightInput.text = sharedPref.getString(getString(R.string.weight), null)

        calculate.setOnClickListener { setCalculateClickListener() }
        reset.setOnClickListener { setErrorClickListener() }

        return root
    }

    private fun setCalculateClickListener() {
        if (isValid(heightInput, weightInput)) {
            saveSharedValue(getString(R.string.height), heightInput.text.toString())
            saveSharedValue(getString(R.string.weight), weightInput.text.toString())

            hideKeyboard()
            calculate()
        }
    }

    private fun setErrorClickListener() {
        hideKeyboard()

        heightInput.text = null
        heightInput.error = null
        weightInput.text = null
        weightInput.error = null
        result.text = null
        resultComponent.visibility = View.GONE
        bar.progress = 0

        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * calculates BMI with formula weight/height^2
     * input units:
     * height - centimeters [converted to meters]
     * weight - kilograms
     */
    private fun calculate() {
        var height = -1f
        var weight = -1f
        try {
            height = heightInput.text.toString().toFloat()
            weight = weightInput.text.toString().toFloat()
        } catch (e: NumberFormatException) {
            System.err.println("Wrong input")
        }

        val res = weight / (height / 100.toDouble()).pow(2.0)
        result.text = String.format("%.2f", res)
        resultComponent.visibility = View.VISIBLE

        drawProgressBar(res)
        saveSharedValue(getString(R.string.bmi_result), result.text as String)
    }

    /**
     * saves value by key to shared preferences
     * @param key property key
     * @param value property value
     */
    private fun saveSharedValue(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    /**
     * sets progress and value of progress bar
     */
    private fun drawProgressBar(res: Double) {
        val multipliedResult: Int = (res * 100).toInt()
        bar.progressDrawable.colorFilter = defineColorFilter(multipliedResult)
        bar.progress = multipliedResult
    }

    /**
     * defines color filter based on input number
     * @param result numeric parameter
     * @return PorterDuffColorFilter
     */
    private fun defineColorFilter(result: Int): PorterDuffColorFilter {
        val color: Int = when (result) {
            in 0..1849 -> Color.BLUE
            in 1850..2499 -> Color.GREEN
            in 2500..2999 -> Color.YELLOW
            in 3000..3499 -> Color.MAGENTA
            else -> Color.RED
        }
        return PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}
