package pl.pjatk.lab3.ui.chart

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import pl.pjatk.lab3.R
import pl.pjatk.lab3.data.CoronaApiService
import pl.pjatk.lab3.data.ServiceBuilder
import pl.pjatk.lab3.model.Country
import pl.pjatk.lab3.model.ResultData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.function.Consumer

class ChartFragment : Fragment() {

    private lateinit var spinner: SearchableSpinner
    private lateinit var dataAdapter: ArrayAdapter<Country>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var drawBtn: Button
    private lateinit var changeSearchCriteriaBtn: Button
    private lateinit var request: CoronaApiService
    private lateinit var chart: LineChart
    private lateinit var chartInputLayout: LinearLayout
    private lateinit var chartContent: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chart, container, false)

        request = ServiceBuilder.buildService(CoronaApiService::class.java)

        chart = root.findViewById(R.id.chart_view)
        chartInputLayout = root.findViewById(R.id.chart_data_input_layout)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return root
        drawBtn = root.findViewById(R.id.draw_chart_btn)
        chartContent = root.findViewById(R.id.chart_content)
        changeSearchCriteriaBtn = root.findViewById(R.id.change_search_criteria_btn)

        val startBtn: Button = root.findViewById(R.id.start_date_btn)
        val endBtn: Button = root.findViewById(R.id.end_date_btn)
        val from: EditText = root.findViewById(R.id.from_text)
        val to: EditText = root.findViewById(R.id.to_text)

        drawBtn.setOnClickListener { drawChart() }
        changeSearchCriteriaBtn.setOnClickListener { changeSearchCriteria() }

        setDatePickerButtonListener(startBtn, from)
        setDatePickerButtonListener(endBtn, to)

        spinner = root.findViewById(R.id.spinner)
        spinner.setTitle(getString(R.string.country_spinner_title))
        getCountries()
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val slug = dataAdapter.getItem(position)
                saveProperty("country", slug!!.Slug)
                Toast.makeText(activity, slug.Slug, Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun changeSearchCriteria() {
        chart.clear()
        chartInputLayout.visibility = VISIBLE
        chartContent.visibility = GONE
    }

    private fun drawChart() {
        val country = sharedPref.getString("country", "")
        val from = sharedPref.getString("from", "")
        val to = sharedPref.getString("to", "")

        val request = ServiceBuilder.buildService(CoronaApiService::class.java)

        val call = request.getChartData(country!!, from!!, to!!)

        call.enqueue(object : Callback<List<ResultData>> {
            override fun onResponse(
                call: Call<List<ResultData>>,
                response: Response<List<ResultData>>
            ) {
                if (response.isSuccessful) {
                    Log.i("API", "Data fetched from API")
                    prepareChart(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<ResultData>>, t: Throwable) {
                Log.e("API", t.message!!)
                Toast.makeText(
                    activity,
                    "Failed to fetch data, try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun prepareChart(results: List<ResultData>) {
        chart.clear()
        val entries: MutableList<Entry> = mutableListOf()

        results.forEach(Consumer { result ->
            entries.add(
                Entry(
                    result.Date.toInstant().toEpochMilli().toFloat(),
                    result.Confirmed.toFloat()
                )
            )
        })

        val dataSet = LineDataSet(entries, "Confirmed cases")

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()

        chartInputLayout.visibility = GONE
        chartContent.visibility = VISIBLE
    }

    private fun setDatePickerButtonListener(btn: Button, edit: EditText) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        btn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val date = "$year-${monthOfYear + 1}-$dayOfMonth"
                    edit.setText(date)
                    when (btn.id) {
                        R.id.start_date_btn -> {
                            saveProperty("from", date)
                        }
                        else -> {
                            saveProperty("to", date)
                        }
                    }
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun saveProperty(key: String, value: String) {
        with(sharedPref) {
            edit()
                .putString(key, value)
                .apply()
        }
        Log.i("TAG", "Saved $key as $value")
    }

    private fun getCountries() {
        val call = request.getCountries()

        call.enqueue(object : Callback<List<Country>> {
            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.isSuccessful) {
                    Log.i("API", "Data fetched from API")
                    fillCountrySpinnerData(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Log.e("API", t.message!!)
                Toast.makeText(
                    activity,
                    "Failed to fetch data, try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fillCountrySpinnerData(countries: List<Country>) {
        dataAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countries
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
    }
}
