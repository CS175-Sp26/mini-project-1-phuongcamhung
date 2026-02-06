package edu.sjsu.android.project1CamPhuong;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

import edu.sjsu.android.project1CamPhuong.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final double tax_insurance = 0.1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());


//set default
        binding.SeekBar.setProgress(100);
        binding.selectTerm.check(R.id.y15);
        binding.SeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SeekBarListener.super.onProgressChanged(seekBar, progress, fromUser);
                {
                    double rate = progress / 10.0;
                    binding.tvInterestValue.setText(String.format(Locale.US, "%.1f%%", rate));
                }
            }
        });

        binding.btnCalculate.setOnClickListener(v -> calculateMortgage());
        binding.btnUninstall.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Mortgage Calculator by Cam Hung")
                    .setMessage("Do you want to uninstall this app?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Open Android's uninstall screen for THIS app
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + getPackageName()));
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
        String principalStr = binding.Principal.getText().toString().trim();
        double principalDou;
        if (principalStr.isEmpty()) {
            binding.tvResult.setText("Press enter the principal.\nThen Press CALCULATE for monthly payments.");
            return;
        }
        if(checkValidInput(principalStr)){
            principalDou = Double.parseDouble(principalStr);
            if(principalDou <= 0){
                binding.tvResult.setText("Principal must be > 0.");
                return;
            }
            int years = getLoanTerms();
            double rate = binding.SeekBar.getProgress() / 10.0;
            int n = years * 12;
            double r = (rate / 100.0) / 12.0;
            boolean withTax = binding.taxesAndInsurance.isChecked();
            double monthlyPayment;
            if (r == 0.0) {
                monthlyPayment = principalDou / n;
            } else {
                double pow = Math.pow(1.0 + r, n);
                monthlyPayment = principalDou * (r * pow) / (pow - 1.0);
            }

            if (withTax) {
                monthlyPayment += monthlyPayment*(1+tax_insurance)/100.0;
            }
            String msg = String.format(Locale.US,"Monthly payment: $%,.2f", monthlyPayment);
            binding.tvResult.setText(msg);
        }
        else{
            binding.tvResult.setText("Please enter a valid number. 2 decimal digits max.\n Then Press CALCULATE for monthly payments.");
        }
    }
    public static boolean checkValidInput(String principal) {
        if (principal == null) return false;
        principal = principal.trim();
        return principal.matches("[+-]?\\d+(\\.\\d{1,2})?");
    }
    private int getLoanTerms() {
        int checkedId = binding.selectTerm.getCheckedRadioButtonId();
        if (checkedId == R.id.y15) return 15;
        if (checkedId == R.id.y20) return 20;
        if (checkedId == R.id.y30) return 30;
        return 0;
    }

}