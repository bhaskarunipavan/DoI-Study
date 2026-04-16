package com.dic.survey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import com.dic.survey.R;
import com.dic.survey.utils.PrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private AutoCompleteTextView spinnerDistrict;
    private MaterialButton btnLogin;
    private TextView tvError;
    private PrefManager prefs;

    private static final String[] DISTRICTS = {
        "Adilabad", "Bhadradri Kothagudem", "Hanamkonda", "Hyderabad",
        "Jagtial", "Jangaon", "Jayashankar Bhupalpally", "Jogulamba Gadwal",
        "Kamareddy", "Karimnagar", "Khammam", "Kumuram Bheem",
        "Mahabubabad", "Mahabubnagar", "Mancherial", "Medak",
        "Medchal Malkajgiri", "Mulugu", "Nagarkurnool", "Nalgonda",
        "Narayanpet", "Nirmal", "Nizamabad", "Peddapalli",
        "Rajanna Sircilla", "Rangareddy", "Sangareddy", "Siddipet",
        "Suryapet", "Vikarabad", "Wanaparthy", "Warangal",
        "Yadadri Bhuvanagiri"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = new PrefManager(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, DISTRICTS);
        spinnerDistrict.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String district = spinnerDistrict.getText() != null ? spinnerDistrict.getText().toString().trim() : "";

        tvError.setVisibility(View.GONE);

        if (username.isEmpty()) { showError("Please enter your Officer ID"); return; }
        if (password.isEmpty()) { showError("Please enter your password"); return; }
        if (district.isEmpty()) { showError("Please select your district"); return; }

        // Offline-capable login: validate locally
        // In production, validate against Supabase Auth when online
        if (username.length() < 3) { showError("Invalid credentials"); return; }

        prefs.setLoggedIn(true);
        prefs.setOfficerId(username);
        prefs.setOfficerName("Officer: " + username);
        prefs.setDistrict(district);
        prefs.setRole("officer");

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
