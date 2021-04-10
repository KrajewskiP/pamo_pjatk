package pl.pjatk.lab2.ui.recipe

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.pjatk.lab2.R

class RecipeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recipe, container, false)
        val bmi: TextView = root.findViewById(R.id.bmi_recipe)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return root
        val bmiResult = sharedPref.getString(getString(R.string.bmi_result), "")
        if (bmiResult.isNullOrBlank()) {
                val view: TextView = root.findViewById(R.id.missing_bmi)
                view.visibility = VISIBLE
            return root
        }
        bmi.text = String.format(getString(R.string.bmi_recipe), bmiResult)

        val fileName = defineFileName(bmiResult)
        val a = activity?.assets?.open("recipe/$fileName.png")

        val imageView: ImageView = root.findViewById(R.id.recipeImageView)
        val bitmap = BitmapFactory.decodeStream(a)
        imageView.setImageBitmap(bitmap)

        val result: LinearLayout = root.findViewById(R.id.resultComponent)
        result.visibility = VISIBLE

        return root
    }

    /**
     * defines file name based on bmi
     * @param bmiResult string representation of BMI resule
     * @return String
     */
    private fun defineFileName(bmiResult: String): String {
        return when ((bmiResult.toDouble() * 100).toInt()) {
            in 0..1849 -> "burger"
            in 1850..2499 -> "pizza"
            in 2500..2999 -> "fish"
            in 3000..3499 -> "light"
            else -> "salad"
        }
    }
}
