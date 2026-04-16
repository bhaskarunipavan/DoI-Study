package com.dic.survey.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.dic.survey.R;
import com.dic.survey.database.SurveyDatabase;
import com.dic.survey.models.SurveyResponse;
import com.dic.survey.utils.FormDataHelper;
import com.dic.survey.utils.NetworkUtils;
import com.dic.survey.utils.PrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseQuestionnaireActivity extends AppCompatActivity {
    protected LinearLayout formContainer;
    protected MaterialButton btnPrevious, btnNext, btnSaveDraft;
    protected TextView tvStepLabel, tvOfflineBanner;
    protected android.widget.ProgressBar progressBar;
    protected int currentSection = 0;
    protected FormDataHelper formData = new FormDataHelper();
    protected PrefManager prefs;
    protected SurveyDatabase db;
    protected ExecutorService executor = Executors.newSingleThreadExecutor();
    protected int draftId = -1;

    protected abstract int getTotalSections();
    protected abstract String getTitle();
    protected abstract String getType();
    protected abstract void renderSection(int section);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        prefs = new PrefManager(this);
        db = SurveyDatabase.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        formContainer = findViewById(R.id.formContainer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        tvStepLabel = findViewById(R.id.tvStepLabel);
        tvOfflineBanner = findViewById(R.id.tvOfflineBanner);
        progressBar = findViewById(R.id.progressBar);
        if (!NetworkUtils.isOnline(this)) tvOfflineBanner.setVisibility(View.VISIBLE);
        btnPrevious.setOnClickListener(v -> navigate(-1));
        btnNext.setOnClickListener(v -> navigate(1));
        btnSaveDraft.setOnClickListener(v -> saveDraft());
        showSection(currentSection);
    }

    protected void navigate(int dir) {
        collectSection();
        int next = currentSection + dir;
        if (next < 0) { finish(); return; }
        if (next >= getTotalSections()) { confirmSubmit(); return; }
        currentSection = next;
        showSection(currentSection);
    }

    protected void showSection(int s) {
        formContainer.removeAllViews();
        btnPrevious.setVisibility(s > 0 ? View.VISIBLE : View.GONE);
        btnNext.setText(s == getTotalSections() - 1 ? "Submit" : "Next");
        tvStepLabel.setText("Section " + (s + 1) + " of " + getTotalSections());
        progressBar.setProgress((int)(((s + 1f) / getTotalSections()) * 100));
        renderSection(s);
        ((androidx.core.widget.NestedScrollView) findViewById(R.id.scrollView)).scrollTo(0, 0);
    }

    protected void header(String t) {
        TextView tv = new TextView(this);
        tv.setText(t); tv.setTextSize(15f);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 28, 0, 4); tv.setLayoutParams(lp);
        View div = new View(this);
        div.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        div.setAlpha(0.25f);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(-1, 2);
        dlp.setMargins(0, 2, 0, 10); div.setLayoutParams(dlp);
        formContainer.addView(tv); formContainer.addView(div);
    }

    protected void label(String t) {
        TextView tv = new TextView(this);
        tv.setText(t); tv.setTextSize(13.5f);
        tv.setTextColor(getResources().getColor(R.color.grey_dark, null));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 12, 0, 4); tv.setLayoutParams(lp);
        formContainer.addView(tv);
    }

    protected TextInputEditText textField(String hint, String key, boolean numeric) {
        TextInputLayout til = new TextInputLayout(this, null,
            com.google.android.material.R.attr.textInputOutlinedStyle);
        til.setHint(hint);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, 4); til.setLayoutParams(lp);
        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTag(key);
        if (numeric) et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String val = formData.getString(key);
        if (!val.isEmpty()) et.setText(val);
        til.addView(et); formContainer.addView(til);
        return et;
    }

    protected TextInputEditText multiField(String hint, String key) {
        TextInputLayout til = new TextInputLayout(this, null,
            com.google.android.material.R.attr.textInputOutlinedStyle);
        til.setHint(hint);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, 4); til.setLayoutParams(lp);
        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTag(key); et.setMinLines(3); et.setMaxLines(7);
        et.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        String val = formData.getString(key);
        if (!val.isEmpty()) et.setText(val);
        til.addView(et); formContainer.addView(til);
        return et;
    }

    protected void checkbox(String key, String lbl) {
        CheckBox cb = new CheckBox(this);
        cb.setText(lbl); cb.setChecked(formData.getBoolean(key)); cb.setTextSize(13f);
        cb.setTag(key);
        cb.setOnCheckedChangeListener((b, c) -> formData.put((String) b.getTag(), c));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 4, 0, 4); cb.setLayoutParams(lp);
        formContainer.addView(cb);
    }

    protected void radio(String key, String[] opts) {
        RadioGroup rg = new RadioGroup(this);
        rg.setTag(key);
        String saved = formData.getString(key);
        for (String opt : opts) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opt); rb.setTextSize(13f); rb.setTag(opt);
            if (opt.equals(saved)) rb.setChecked(true);
            rg.addView(rb);
        }
        rg.setOnCheckedChangeListener((g, id) -> {
            RadioButton rb = g.findViewById(id);
            if (rb != null) formData.put((String) g.getTag(), (String) rb.getTag());
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 4, 0, 8); rg.setLayoutParams(lp);
        formContainer.addView(rg);
    }

    protected void collectSection() {
        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View v = formContainer.getChildAt(i);
            if (v instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) v;
                if (til.getEditText() != null && til.getEditText().getTag() instanceof String)
                    formData.put((String) til.getEditText().getTag(), til.getEditText().getText().toString());
            }
        }
    }

    protected void saveDraft() {
        collectSection();
        executor.execute(() -> {
            SurveyResponse r = buildResponse(SurveyResponse.STATUS_DRAFT);
            if (draftId >= 0) { r.id = draftId; db.surveyDao().update(r); }
            else draftId = (int) db.surveyDao().insert(r);
            runOnUiThread(() -> Snackbar.make(formContainer, "Draft saved", Snackbar.LENGTH_SHORT).show());
        });
    }

    protected void confirmSubmit() {
        collectSection();
        new AlertDialog.Builder(this).setTitle("Submit Response")
            .setMessage("Submit this questionnaire? It will sync when online.")
            .setPositiveButton("Submit", (d, w) -> submitResponse())
            .setNegativeButton("Cancel", null).show();
    }

    protected void submitResponse() {
        executor.execute(() -> {
            SurveyResponse r = buildResponse(SurveyResponse.STATUS_SUBMITTED);
            r.submittedAt = System.currentTimeMillis();
            if (draftId >= 0) { r.id = draftId; db.surveyDao().update(r); }
            else db.surveyDao().insert(r);
            runOnUiThread(() -> {
                Snackbar.make(formContainer, "Submitted successfully!", Snackbar.LENGTH_LONG).show();
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::finish, 1500);
            });
        });
    }

    protected SurveyResponse buildResponse(String status) {
        SurveyResponse r = new SurveyResponse();
        r.questionnaireType = getType();
        r.district = formData.getString("district"); if (r.district.isEmpty()) r.district = prefs.getDistrict();
        r.respondentName = prefs.getOfficerName();
        r.formData = formData.toJson(); r.status = status;
        r.officerId = prefs.getOfficerId();
        r.createdAt = r.updatedAt = System.currentTimeMillis();
        return r;
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
