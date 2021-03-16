package pl.pjatk.bmi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView height;
    private TextView weight;
    private LinearLayout resultComponent;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        height = findViewById(R.id.inputHeight);
        weight = findViewById(R.id.inputWeight);
        resultComponent = findViewById(R.id.resultComponent);
        result = findViewById(R.id.result);

        Button calculate = findViewById(R.id.calculate);
        Button reset = findViewById(R.id.reset);

        calculate.setOnClickListener(v -> {
            if (isValidated(height, weight)) {
                calculate();
            }
        });

        reset.setOnClickListener(v -> {
            height.setText(null);
            height.setError(null);
            weight.setText(null);
            weight.setError(null);

            result.setText(null);
            resultComponent.setVisibility(View.GONE);
        });
    }

    /**
     * validates views and setting errors in case of wrong input
     * @param views input views
     * @return true/false
     */
    private boolean isValidated(final View...views) {
        boolean validated = true;
        for (View view : views) {
            TextView v = (TextView) view;
            if (v.getText().toString().length() == 0) {
                v.setError("Field is required");
                validated = false;
            } else if (!(Float.parseFloat(v.getText().toString()) > 0)) {
                v.setError("Value should be a positive number");
                validated = false;
            }
        }
        return validated;
    }

    /**
     * calculates BMI with formula weight/height^2
     * input units:
     *  height - centimeters [converted to meters]
     *  weight - kilograms
     */
    private void calculate() {

        float height = -1;
        float weight = -1;
        try {
            height = Float.parseFloat(this.height.getText().toString());
            weight = Float.parseFloat(this.weight.getText().toString());
        } catch (NumberFormatException e) {
            System.err.println("Wrong input");
        }

        Double res = weight / Math.pow(height/100, 2);

        result.setText(String.format(Locale.getDefault(), "%.2f", res));
        resultComponent.setVisibility(View.VISIBLE);
    }
}
