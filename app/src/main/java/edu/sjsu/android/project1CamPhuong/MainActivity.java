package edu.sjsu.android.project1CamPhuong;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

import edu.sjsu.android.project1CamPhuong.databinding.ActivityMainBinding;

//import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EditText principal;
    private SeekBar interestRateBar;
    private TextView interestValue;
    private RadioGroup loanTerm;
    private CheckBox includeTaxes;
    private Button calculate;
    private Button uninstall;
    private static final double tax_insurance = 0.1;
    private TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        View root = binding.getRoot();
        setContentView(R.layout.activity_main);

        //connect java file with ui stuff
        principal = findViewById(R.id.Principal);
        interestRateBar = findViewById(R.id.seekInterest);

//        int blue = getResources().getColor(R.color.blue_seek, getTheme());
//        interestRateBar.getProgressDrawable().setTint(blue);
//        interestRateBar.getThumb().setTint(blue);

        interestValue = findViewById(R.id.tvInterestValue);
        loanTerm = findViewById(R.id.selectGr);
        includeTaxes = findViewById(R.id.taxesAndInsurance);
        calculate = findViewById(R.id.Calculate);
        uninstall = findViewById(R.id.Uninstall);
        result = findViewById(R.id.tvResult);


//set default
        interestRateBar.setProgress(1000);
        loanTerm.check(R.id.y15);

        interestRateBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double rate = progress / 100.0;
                interestValue.setText(String.format(java.util.Locale.US, "%.2f%%", rate));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        calculate.setOnClickListener(v -> calculateMortgage());
        uninstall.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Mortgage Calculator by Cam Hung")
                    .setMessage("Do you want to uninstall this app?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Open Android's uninstall screen for THIS app
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DELETE);
                        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void calculateMortgage(){
        String principalStr = principal.getText().toString().trim();
        double principalDou;
        if (principalStr.isEmpty()) {
            result.setText("Press enter the principal.\nThen Press CALCULATE for monthly payments.");
            return;
        }
        if(checkValidInput(principalStr)){
            principalDou = Double.parseDouble(principalStr);
            if(principalDou <= 0){
                result.setText("Principal must be > 0.");
                return;
            }
            int years = getLoanTerms();
            double rate = interestRateBar.getProgress() / 100.0;
            int n = years * 12;
            double r = (rate / 100.0) / 12.0;

            double monthlyPayment;
            if (r == 0.0) {
                monthlyPayment = principalDou / n;
            } else {
                double pow = Math.pow(1.0 + r, n);
                monthlyPayment = principalDou * (r * pow) / (pow - 1.0);
            }

            if (includeTaxes.isChecked()) {
                monthlyPayment += monthlyPayment*(1+tax_insurance)/100.0;
            }
            String msg = String.format(Locale.US,"Monthly payment: $%,.2f", monthlyPayment);
            result.setText(msg);
        }
        else{
            result.setText("Please enter a valid number. 2 decimal digits max.\n Then Press CALCULATE for monthly payments.");
        }
    }
    public static boolean checkValidInput(String principal) {
        if (principal == null) return false;
        principal = principal.trim();
        return principal.matches("[+-]?\\d+(\\.\\d{1,2})?");
    }
    private int getLoanTerms() {
        int checkedId = loanTerm.getCheckedRadioButtonId();
        if (checkedId == R.id.y15) return 15;
        if (checkedId == R.id.y20) return 20;
        if (checkedId == R.id.y30) return 30;
        return 0;
    }

}