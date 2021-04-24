package pl.pjatk.lab3.ui.bmr

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.pjatk.lab3.R
import pl.pjatk.lab3.util.isValid

class BmrFragment : Fragment() {

    private val TAG = "BMR"

    private lateinit var heightInput: TextView
    private lateinit var weightInput: TextView
    private lateinit var ageInput: TextView
    private lateinit var resultComponent: LinearLayout
    private lateinit var resultBmr: TextView
    private lateinit var resultTee: TextView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var radioGenderGroup: RadioGroup
    private lateinit var activityList: Spinner

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_bmr, container, false)
        val calculate: Button = root.findViewById(R.id.calculate)
        val reset: Button = root.findViewById(R.id.reset)

        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return root

        initializeViews(root)

        calculate.setOnClickListener { setCalculateClickListener() }
        reset.setOnClickListener { setResetClickListener() }

        return root
    }

    private fun initializeViews(root: View) {
        heightInput = root.findViewById(R.id.inputHeight)
        weightInput = root.findViewById(R.id.inputWeight)
        resultComponent = root.findViewById(R.id.resultComponent)
        ageInput = root.findViewById(R.id.inputAge)
        resultBmr = root.findViewById(R.id.result_bmr)
        resultTee = root.findViewById(R.id.result_tee)
        radioGenderGroup = root.findViewById(R.id.radioGender)
        activityList = root.findViewById(R.id.activity_list)

        heightInput.text = sharedPref.getString(getString(R.string.height), null)
        weightInput.text = sharedPref.getString(getString(R.string.weight), null)
        ageInput.text = sharedPref.getString(getString(R.string.age), null)
    }

    private fun setCalculateClickListener() {
        if (isValid(heightInput, weightInput, ageInput)) {
            saveSharedValue(getString(R.string.height), heightInput.text.toString())
            saveSharedValue(getString(R.string.weight), weightInput.text.toString())
            saveSharedValue(getString(R.string.age), ageInput.text.toString())

            hideKeyboard()
            calculate()
        }
    }

    private fun setResetClickListener() {
        hideKeyboard()

        heightInput.text = null
        heightInput.error = null
        weightInput.text = null
        weightInput.error = null
        ageInput.text = null
        ageInput.error = null
        resultBmr.text = null
        resultTee.text = null
        resultComponent.visibility = View.GONE
        sharedPref.edit().clear().apply()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
     * calculates BMR (basal metabolic rate) with formula:
     * 10 * weight(kg) + 6.25 * height(cm) - 5 * age(years) + gender factor: men(-161), women(+5)
     * and TEE (total energy expenditure) with formula:
     * BMR * activity factor
     * activity factor:
     * 1.2 - no/little exercises
     * 1.375 - light exercise 1–3 times a week
     * 1.55 - moderate exercise 3–5 times a week
     * 1.725 - hard exercise 6–7 times a week
     * 1.9 - hard exercise and physical job
     */
    private fun calculate() {
        var height = -1
        var weight = -1
        var age = -1
        try {
            height = heightInput.text.toString().toInt()
            weight = weightInput.text.toString().toInt()
            age = ageInput.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Wrong input, " + e.message)
        }
        val genderFactor = getGenderFactor()
        val bmr = 10 * weight + 6.25 * height - 5 * age + genderFactor
        resultBmr.text = String.format(getString(R.string.result_bmr), bmr)

        val activityFactor = getActivityFactor()
        val tee = bmr * activityFactor
        resultTee.text = String.format(getString(R.string.result_tee), tee)
        resultComponent.visibility = View.VISIBLE

        saveSharedValue(getString(R.string.result_tee), tee.toString())
    }

    private fun getGenderFactor(): Int {
        val selectedId: Int = radioGenderGroup.checkedRadioButtonId
        return if (selectedId == R.id.radioFemale) -161 else 5
    }

    private fun getActivityFactor(): Float {
        return when (activityList.selectedItemPosition) {
            0 -> 1.2f
            1 -> 1.375f
            2 -> 1.55f
            3 -> 1.725f
            else -> 1.9f
        }
    }
}
