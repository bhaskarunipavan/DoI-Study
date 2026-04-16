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

public class DICQuestionnaireActivity extends AppCompatActivity {

    private LinearLayout formContainer;
    private MaterialButton btnPrevious, btnNext, btnSaveDraft;
    private TextView tvStepLabel, tvOfflineBanner;
    private android.widget.ProgressBar progressBar;
    private int currentSection = 0;
    private static final int TOTAL_SECTIONS = 8;
    private FormDataHelper formData = new FormDataHelper();
    private PrefManager prefs;
    private SurveyDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private int draftId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        prefs = new PrefManager(this);
        db = SurveyDatabase.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("DoI / DIC Questionnaire");
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
        renderSection(currentSection);
    }

    private void navigate(int dir) {
        collectSection();
        int next = currentSection + dir;
        if (next < 0) { finish(); return; }
        if (next >= TOTAL_SECTIONS) { confirmSubmit(); return; }
        currentSection = next;
        renderSection(currentSection);
    }

    private void renderSection(int s) {
        formContainer.removeAllViews();
        btnPrevious.setVisibility(s > 0 ? View.VISIBLE : View.GONE);
        btnNext.setText(s == TOTAL_SECTIONS - 1 ? "Submit" : "Next");
        tvStepLabel.setText("Section " + (s + 1) + " of " + TOTAL_SECTIONS);
        progressBar.setProgress((int)(((s + 1f) / TOTAL_SECTIONS) * 100));
        switch (s) {
            case 0: buildDistrictProfile(); break;
            case 1: buildIndustrialProfile(); break;
            case 2: buildInstitutional(); break;
            case 3: buildLandInfra(); break;
            case 4: buildFinance(); break;
            case 5: buildLabour(); break;
            case 6: buildRMTech(); break;
            case 7: buildMarketsGeneral(); break;
        }
        ((androidx.core.widget.NestedScrollView) findViewById(R.id.scrollView)).scrollTo(0, 0);
    }

    private void buildDistrictProfile() {
        header("District Profile");
        label("Name of the District *");
        textField("District name", "district_name", false);
        header("Predominant Economic Activity (%)");
        label("Agriculture %"); textField("e.g. 40", "agri_pct", true);
        label("Dairy and Poultry %"); textField("e.g. 10", "dairy_pct", true);
        label("Mining %"); textField("e.g. 5", "mining_pct", true);
        label("Manufacturing Industries %"); textField("e.g. 25", "mfg_pct", true);
        label("Service Sector %"); textField("e.g. 20", "service_pct", true);
        header("Natural Resources & Raw Materials");
        label("Key natural resources and underutilized raw materials with reasons");
        multiField("List minerals, agriculture, forest, water, energy resources", "resources_raw_materials");
        header("Future Industrial Potential");
        label("Future industries that can be set up based on existing resources");
        multiField("e.g. Agriculture → Food processing | Dairy → Dairy processing | Minerals → Cement", "future_potential");
    }

    private void buildIndustrialProfile() {
        header("Industrial Unit Count");
        label("Number of active Large Units"); textField("Count", "large_units", true);
        label("Number of active Medium Units"); textField("Count", "medium_units", true);
        label("Number of active Small Units"); textField("Count", "small_units", true);
        label("Number of active Micro Units"); textField("Count", "micro_units", true);
        label("Sick / closed units and reasons"); multiField("Type, count, sector, reasons", "sick_units");
        header("Unregistered Units");
        label("Approximate unregistered entities and reasons");
        multiField("Industry type, unregistered count, reasons", "unregistered_units");
        header("Sectoral Clusters");
        label("Sectoral clusters / IDAs / TGIIC parks present?");
        radio("clusters_present", new String[]{"Yes","No"});
        label("Cluster details (name, sector, area, units, infrastructure adequacy)");
        multiField("Cluster details", "cluster_details");
    }

    private void buildInstitutional() {
        header("Institutional Challenges");
        label("Internal Capacity of DIC (staff, IT systems, training gaps)");
        multiField("Describe challenges", "dic_capacity");
        label("Promotion of Entrepreneurship (investment, mentorship, startup awareness)");
        multiField("Describe challenges", "entrepreneurship");
        label("Single Window System & Clearances (delays, coordination)");
        multiField("Describe challenges", "single_window");
        label("Support to MSMEs (infrastructure, working capital, technology, markets)");
        multiField("Describe challenges", "msme_support");
        label("Implementation of Government Schemes (skill, subsidy disbursements)");
        multiField("Describe challenges", "govt_schemes");
        header("Cluster Development Programme Barriers");
        checkbox("cdp_land", "Limited Land Availability");
        checkbox("cdp_low_msme", "Low MSME participation");
        checkbox("cdp_delays", "Delays in approvals");
        checkbox("cdp_no_consultancy", "Insufficient technical consultancy support");
    }

    private void buildLandInfra() {
        header("Access to Land");
        label("Challenges in acquiring industrial land");
        multiField("Availability, affordability, underutilization, allotment process", "land_challenges");
        header("Infrastructure Challenges");
        label("Power (outages, voltage, tariffs, connection delays)");
        multiField("Power challenges", "power_challenges");
        label("Industrial Water (supply, shortages, pipelines, cost)");
        multiField("Water challenges", "water_challenges");
        label("Roads & Connectivity (internal roads, last-mile, highway linkage)");
        multiField("Road challenges", "road_challenges");
        label("Common Facilities (ETP, testing labs, warehousing)");
        multiField("Common facility gaps", "common_facility_gaps");
        label("Drainage & Sewerage");
        multiField("Drainage challenges", "drainage_challenges");
        label("Logistics (warehousing, cold storage, ICD/dry port, freight centres)");
        multiField("Logistics gaps", "logistics_challenges");
        label("Social Infrastructure near IDAs (housing, water, sanitation, healthcare, schools)");
        multiField("Social infrastructure gaps", "social_infra");
        header("Flatted Factory & Energy");
        label("Potential for flatted-factory industrial infrastructure");
        multiField("Readiness, locations, enablers, constraints", "flatted_factory");
        label("Renewable energy utilization %"); textField("e.g. 15", "renewable_pct", true);
        label("Captive power utilization %"); textField("e.g. 10", "captive_pct", true);
    }

    private void buildFinance() {
        header("Access to Finance & Credit");
        label("Average loan disbursement time for T-IDEA");
        radio("tidea_duration", new String[]{"<15 days","15–30 days","30–45 days","45–60 days",">60 days"});
        label("Average loan disbursement time for T-PRIDE");
        radio("tpride_duration", new String[]{"<15 days","15–30 days","30–45 days","45–60 days",">60 days"});
        label("Challenges in loan/subsidy disbursement");
        multiField("Process, documentation, release timeline challenges", "finance_challenges");
        header("MSME Informal Finance");
        label("Proportion of MSMEs relying on informal finance sources");
        radio("informal_finance", new String[]{"<10%","10–25%","25–50%","50–75%",">75%"});
        header("Challenges in Accessing Loans/Subsidies");
        checkbox("fin_awareness", "Lack of awareness on loans/subsidies");
        checkbox("fin_eligibility", "Eligibility criteria issues");
        checkbox("fin_interest", "High interest rates");
        checkbox("fin_collateral", "Collateral requirements");
        checkbox("fin_processing", "Processing timelines");
        label("Level of awareness among industries on industrial loans/subsidies");
        radio("finance_awareness", new String[]{"<25%","25–50%","51–75%",">75%"});
        label("Recommended interventions to improve finance access");
        multiField("Awareness, simplify process, handholding, etc.", "finance_interventions");
    }

    private void buildLabour() {
        header("Access to Labour & Skills");
        label("Shortage of skilled industrial labour (sector, job role, reasons)");
        multiField("Sector | Job role | Reasons for shortage", "skilled_shortage");
        label("Shortage of semi-skilled labour (sector, job role, reasons)");
        multiField("Sector | Job role | Reasons for shortage", "semiskilled_shortage");
        label("Challenges in recruiting skilled & semi-skilled workforce");
        multiField("Youth unavailability, low wages, migration, housing, etc.", "recruitment_challenges");
        header("Skill Gaps");
        label("Major gaps in district-level skilling ecosystem");
        multiField("Curriculum mismatch, outdated trades, placement gaps, trainer quality", "skill_gaps");
        label("Awareness of skill development programs among youth and industries");
        radio("skill_awareness", new String[]{"<10%","10–25%","25–50%",">50%"});
        header("Active Government Skill Programs in District");
        checkbox("skill_task", "TASK"); checkbox("skill_tssdm", "TSSDM");
        checkbox("skill_esdp", "ESDP (Entrepreneurship Skill Development Programme)");
        checkbox("skill_samarth", "Samarth Scheme (Textiles)");
        checkbox("skill_pmfme", "PMFME Training Component");
        checkbox("skill_wehub", "WE-Hub Programs");
        checkbox("skill_aspire", "ASPIRE");
        header("Skilling Institutes");
        label("Number of ITIs"); textField("Count", "iti_count", true);
        label("Number of Polytechnic colleges"); textField("Count", "poly_count", true);
        label("Issues with skilling institutes");
        multiField("Infrastructure, curriculum, placement, trainer quality", "institute_issues");
        label("Measures to promote women and PwD inclusion");
        multiField("Current measures and suggestions", "inclusion_measures");
        label("Suggested interventions for overall workforce improvement");
        multiField("Interventions to improve workforce and skilling", "workforce_interventions");
    }

    private void buildRMTech() {
        header("Access to Raw Materials");
        label("Prominent raw materials, source, challenges and interventions");
        multiField("RM name | Source (intra/inter-state/import) | Issues | Interventions", "rm_details");
        label("Awareness of DIC support for RM procurement");
        radio("rm_dic_awareness", new String[]{"<25%","25–50%","51–75%",">75%"});
        label("Awareness of RM financial assistance schemes (NSIC, T-IDEA, TG Logistics Policy)");
        radio("rm_scheme_awareness", new String[]{"<25%","25–50%","51–75%",">75%"});
        header("Access to Technology & Innovation");
        label("Percentage of MSMEs relying on manual or semi-automated processes");
        radio("manual_process_pct", new String[]{"<25%","25–50%","51–75%",">75%"});
        label("Innovation ecosystem facilities available (tech centres, CFCs, CoEs)");
        multiField("List available facilities", "innovation_facilities");
        header("Technology Upgradation Scheme Beneficiaries");
        label("MSME Champions (CLCS-TUS)"); textField("Count", "tech_clcs", true);
        label("T-IDEA beneficiaries"); textField("Count", "tech_tidea", true);
        label("T-PRIDE beneficiaries"); textField("Count", "tech_tpride", true);
        label("Yantram Fund beneficiaries"); textField("Count", "tech_yantram", true);
        label("ZED Green Manufacturing beneficiaries"); textField("Count", "tech_zed", true);
        header("Technology Adoption Barriers");
        checkbox("tech_highcost", "High costs"); checkbox("tech_skillgap", "Skills gap");
        checkbox("tech_infra", "Infrastructure limitations"); checkbox("tech_rd", "Limited access to R&D");
        label("Interventions to accelerate technology adoption");
        multiField("Suggested interventions", "tech_interventions");
    }

    private void buildMarketsGeneral() {
        header("Access to Markets & Trade Promotion");
        label("Existing trade markets for major industrial products");
        checkbox("mkt_local", "Local markets (within district)");
        checkbox("mkt_state", "State-level markets");
        checkbox("mkt_national", "National markets");
        checkbox("mkt_export", "International/export markets");
        label("E-commerce platform awareness level");
        radio("ecomm_awareness", new String[]{"<10%","10–25%","25–50%","50–75%",">75%"});
        label("GeM portal registration level among industries");
        radio("gem_reg", new String[]{"<10%","10–25%","25–50%","50–75%",">75%"});
        label("Dry ports or multimodal logistics facilities available?");
        radio("dry_port", new String[]{"Yes","No"});
        label("Distance from district industrial parks to nearest NH/SH");
        radio("nh_distance", new String[]{"1–3 km","3–10 km","10–20 km",">20 km"});
        label("Industries with export potential but not currently exporting");
        multiField("Industry type | Product | Issues | Solutions", "export_potential");
        label("Immediate interventions to expand market access");
        multiField("Suggested domestic and international market interventions", "market_interventions");
        header("General — Growth & Investment");
        label("Overall industrial growth potential over next 5–10 years");
        multiField("Key drivers, sectors, supporting factors", "growth_potential");
        label("Which sectors have highest potential for growth/investment?");
        multiField("Sectors with justification", "high_potential_sectors");
        label("What restricts new industrial investment?");
        checkbox("restrict_land", "Land availability issues");
        checkbox("restrict_infra", "Infrastructure gaps");
        checkbox("restrict_regulatory", "Regulatory delays");
        checkbox("restrict_resources", "Limited resources availability");
        checkbox("restrict_market", "Market access");
        checkbox("restrict_branding", "Poor district-level branding");
        label("Most urgent interventions required");
        multiField("Priority interventions", "key_interventions");
        label("Barriers for first-generation entrepreneurs");
        checkbox("barrier_mentoring", "Lack of mentoring support");
        checkbox("barrier_social", "Social/market access constraints");
        checkbox("barrier_awareness", "Low awareness of support schemes");
        label("Key sector-specific challenges and solutions");
        multiField("Sector | Major challenges | Suggested interventions", "sector_challenges");
        label("Current status of industrial waste management infrastructure");
        multiField("Waste management status and gaps", "waste_mgmt");
    }

    // ── UI Helpers ──────────────────────────────────────────────────────────

    private void header(String t) {
        TextView tv = new TextView(this);
        tv.setText(t);
        tv.setTextSize(15f);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 28, 0, 4);
        tv.setLayoutParams(lp);
        View div = new View(this);
        div.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        div.setAlpha(0.25f);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(-1, 2);
        dlp.setMargins(0, 2, 0, 10);
        div.setLayoutParams(dlp);
        formContainer.addView(tv);
        formContainer.addView(div);
    }

    private void label(String t) {
        TextView tv = new TextView(this);
        tv.setText(t);
        tv.setTextSize(13.5f);
        tv.setTextColor(getResources().getColor(R.color.grey_dark, null));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 12, 0, 4);
        tv.setLayoutParams(lp);
        formContainer.addView(tv);
    }

    private TextInputEditText textField(String hint, String key, boolean numeric) {
        TextInputLayout til = new TextInputLayout(this, null,
            com.google.android.material.R.attr.textInputOutlinedStyle);
        til.setHint(hint);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, 4);
        til.setLayoutParams(lp);
        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTag(key);
        if (numeric) et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String val = formData.getString(key);
        if (!val.isEmpty()) et.setText(val);
        til.addView(et);
        formContainer.addView(til);
        return et;
    }

    private TextInputEditText multiField(String hint, String key) {
        TextInputLayout til = new TextInputLayout(this, null,
            com.google.android.material.R.attr.textInputOutlinedStyle);
        til.setHint(hint);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, 4);
        til.setLayoutParams(lp);
        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTag(key);
        et.setMinLines(3);
        et.setMaxLines(7);
        et.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        String val = formData.getString(key);
        if (!val.isEmpty()) et.setText(val);
        til.addView(et);
        formContainer.addView(til);
        return et;
    }

    private void checkbox(String key, String label) {
        CheckBox cb = new CheckBox(this);
        cb.setText(label);
        cb.setChecked(formData.getBoolean(key));
        cb.setTextSize(13f);
        cb.setTag(key);
        cb.setOnCheckedChangeListener((b, c) -> formData.put((String) b.getTag(), c));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 4, 0, 4);
        cb.setLayoutParams(lp);
        formContainer.addView(cb);
    }

    private void radio(String key, String[] opts) {
        RadioGroup rg = new RadioGroup(this);
        rg.setTag(key);
        String saved = formData.getString(key);
        for (String opt : opts) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opt);
            rb.setTextSize(13f);
            rb.setTag(opt);
            if (opt.equals(saved)) rb.setChecked(true);
            rg.addView(rb);
        }
        rg.setOnCheckedChangeListener((g, id) -> {
            RadioButton rb = g.findViewById(id);
            if (rb != null) formData.put((String) g.getTag(), (String) rb.getTag());
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 4, 0, 8);
        rg.setLayoutParams(lp);
        formContainer.addView(rg);
    }

    private void collectSection() {
        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View v = formContainer.getChildAt(i);
            if (v instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) v;
                if (til.getEditText() != null && til.getEditText().getTag() instanceof String) {
                    formData.put((String) til.getEditText().getTag(),
                        til.getEditText().getText().toString());
                }
            }
        }
    }

    private void saveDraft() {
        collectSection();
        executor.execute(() -> {
            SurveyResponse r = buildResponse(SurveyResponse.STATUS_DRAFT);
            if (draftId >= 0) { r.id = draftId; db.surveyDao().update(r); }
            else draftId = (int) db.surveyDao().insert(r);
            runOnUiThread(() -> Snackbar.make(formContainer, "Draft saved", Snackbar.LENGTH_SHORT).show());
        });
    }

    private void confirmSubmit() {
        collectSection();
        new AlertDialog.Builder(this)
            .setTitle("Submit Response")
            .setMessage("Submit this DIC questionnaire? It will sync when online.")
            .setPositiveButton("Submit", (d, w) -> submitResponse())
            .setNegativeButton("Cancel", null).show();
    }

    private void submitResponse() {
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

    private SurveyResponse buildResponse(String status) {
        SurveyResponse r = new SurveyResponse();
        r.questionnaireType = SurveyResponse.TYPE_DIC;
        r.district = formData.getString("district_name");
        if (r.district.isEmpty()) r.district = prefs.getDistrict();
        r.respondentName = prefs.getOfficerName();
        r.formData = formData.toJson();
        r.status = status;
        r.officerId = prefs.getOfficerId();
        r.createdAt = r.updatedAt = System.currentTimeMillis();
        return r;
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
