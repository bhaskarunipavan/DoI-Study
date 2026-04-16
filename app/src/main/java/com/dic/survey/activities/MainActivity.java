package com.dic.survey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import com.dic.survey.R;
import com.dic.survey.database.SurveyDatabase;
import com.dic.survey.sync.SyncManager;
import com.dic.survey.utils.NetworkUtils;
import com.dic.survey.utils.PrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvSyncStatus, tvOfficerName, tvDistrictName, tvTotalCount;
    private MaterialButton btnSyncNow;
    private PrefManager prefs;
    private SyncManager syncManager;
    private SurveyDatabase db;
    private boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new PrefManager(this);
        if (!prefs.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = SurveyDatabase.getInstance(this);
        syncManager = new SyncManager(this);

        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        tvOfficerName = findViewById(R.id.tvOfficerName);
        tvDistrictName = findViewById(R.id.tvDistrictName);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        btnSyncNow = findViewById(R.id.btnSyncNow);

        tvOfficerName.setText(prefs.getOfficerName());
        tvDistrictName.setText(prefs.getDistrict() + " District");

        // Observe total response count
        db.surveyDao().getAllResponses().observe(this, responses -> {
            tvTotalCount.setText(String.valueOf(responses != null ? responses.size() : 0));
        });

        // Observe pending sync count
        db.surveyDao().getPendingSyncCount().observe(this, count -> {
            updateSyncStatus(count != null ? count : 0);
        });

        // Network monitoring
        isOnline = NetworkUtils.isOnline(this);
        NetworkUtils.registerNetworkCallback(this, new NetworkUtils.NetworkCallback() {
            @Override
            public void onAvailable() {
                runOnUiThread(() -> {
                    isOnline = true;
                    Snackbar.make(findViewById(R.id.btnViewResponses),
                        "Back online — syncing…", Snackbar.LENGTH_SHORT).show();
                    triggerSync();
                });
            }
            @Override
            public void onLost() {
                runOnUiThread(() -> {
                    isOnline = false;
                    tvSyncStatus.setText("Offline — responses saved locally");
                });
            }
        });

        btnSyncNow.setOnClickListener(v -> triggerSync());

        // Questionnaire card clicks
        findViewById(R.id.cardDIC).setOnClickListener(v -> openQuestionnaire("DIC"));
        findViewById(R.id.cardEnterprise).setOnClickListener(v -> openQuestionnaire("ENTERPRISE"));
        findViewById(R.id.cardAssociation).setOnClickListener(v -> openQuestionnaire("ASSOCIATION"));
        findViewById(R.id.cardStakeholder).setOnClickListener(v -> openQuestionnaire("STAKEHOLDER"));
        findViewById(R.id.btnViewResponses).setOnClickListener(v ->
            startActivity(new Intent(this, ResponsesActivity.class)));
        findViewById(R.id.btnMISReport).setOnClickListener(v ->
            startActivity(new Intent(this, MISReportActivity.class)));

        // Auto-sync on start if online
        if (isOnline) triggerSync();
    }

    private void openQuestionnaire(String type) {
        Intent intent;
        switch (type) {
            case "DIC": intent = new Intent(this, DICQuestionnaireActivity.class); break;
            case "ENTERPRISE": intent = new Intent(this, EnterpriseQuestionnaireActivity.class); break;
            case "ASSOCIATION": intent = new Intent(this, AssociationQuestionnaireActivity.class); break;
            default: intent = new Intent(this, StakeholderQuestionnaireActivity.class); break;
        }
        startActivity(intent);
    }

    private void triggerSync() {
        if (!isOnline) {
            Snackbar.make(findViewById(R.id.btnViewResponses),
                "No connection — will sync when online", Snackbar.LENGTH_SHORT).show();
            return;
        }
        tvSyncStatus.setText("Syncing…");
        syncManager.syncPending(new SyncManager.SyncCallback() {
            @Override
            public void onSyncComplete(int synced, int failed) {
                String msg = synced > 0 ? synced + " response(s) synced" : "All responses synced";
                Snackbar.make(findViewById(R.id.btnViewResponses), msg, Snackbar.LENGTH_SHORT).show();
            }
            @Override
            public void onSyncError(String error) {
                tvSyncStatus.setText("Sync error — " + error);
            }
        });
    }

    private void updateSyncStatus(int pendingCount) {
        if (!isOnline) {
            tvSyncStatus.setText("Offline · " + pendingCount + " pending");
        } else if (pendingCount == 0) {
            tvSyncStatus.setText("Online · All synced ✓");
        } else {
            tvSyncStatus.setText("Online · " + pendingCount + " pending sync");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Settings").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 2, 0, "Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 2) {
            prefs.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
