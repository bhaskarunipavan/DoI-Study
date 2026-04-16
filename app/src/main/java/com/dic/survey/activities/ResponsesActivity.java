package com.dic.survey.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dic.survey.R;
import com.dic.survey.adapters.ResponsesAdapter;
import com.dic.survey.database.SurveyDatabase;
import com.dic.survey.models.SurveyResponse;
import com.dic.survey.sync.SyncManager;
import com.dic.survey.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResponsesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ResponsesAdapter adapter;
    private SurveyDatabase db;
    private SyncManager syncManager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<SurveyResponse> allResponses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = SurveyDatabase.getInstance(this);
        syncManager = new SyncManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResponsesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        db.surveyDao().getAllResponses().observe(this, responses -> {
            allResponses = responses != null ? responses : new ArrayList<>();
            updateList(allResponses);
        });

        // Chip filters
        View.OnClickListener filterClick = v -> {
            String type = null;
            if (v.getId() == R.id.chipDIC) type = "DIC";
            else if (v.getId() == R.id.chipEnterprise) type = "ENTERPRISE";
            else if (v.getId() == R.id.chipAssociation) type = "ASSOCIATION";
            else if (v.getId() == R.id.chipStakeholder) type = "STAKEHOLDER";
            final String filterType = type;
            if (filterType == null) { updateList(allResponses); return; }
            List<SurveyResponse> filtered = new ArrayList<>();
            for (SurveyResponse r : allResponses) if (filterType.equals(r.questionnaireType)) filtered.add(r);
            updateList(filtered);
        };
        findViewById(R.id.chipAll).setOnClickListener(filterClick);
        findViewById(R.id.chipDIC).setOnClickListener(filterClick);
        findViewById(R.id.chipEnterprise).setOnClickListener(filterClick);
        findViewById(R.id.chipAssociation).setOnClickListener(filterClick);
        findViewById(R.id.chipStakeholder).setOnClickListener(filterClick);

        ((MaterialButton) findViewById(R.id.btnSyncAll)).setOnClickListener(v -> {
            if (!NetworkUtils.isOnline(this)) {
                Snackbar.make(recyclerView, "No connection — will sync when online", Snackbar.LENGTH_SHORT).show();
                return;
            }
            syncManager.syncPending(new SyncManager.SyncCallback() {
                @Override public void onSyncComplete(int synced, int failed) {
                    Snackbar.make(recyclerView, synced + " response(s) synced", Snackbar.LENGTH_SHORT).show();
                }
                @Override public void onSyncError(String error) {
                    Snackbar.make(recyclerView, "Sync error: " + error, Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        ((MaterialButton) findViewById(R.id.btnExport)).setOnClickListener(v -> exportCSV());
    }

    private void updateList(List<SurveyResponse> list) {
        adapter.updateData(list);
        recyclerView.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void exportCSV() {
        executor.execute(() -> {
            try {
                File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                if (dir == null) dir = getFilesDir();
                String fname = "dic_survey_export_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
                File csvFile = new File(dir, fname);
                FileWriter fw = new FileWriter(csvFile);
                fw.write("ID,Type,District,Respondent,Status,Created,Submitted,OfficerID\n");
                List<SurveyResponse> all = db.surveyDao().getAllResponsesSync();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                for (SurveyResponse r : all) {
                    fw.write(r.id + ",");
                    fw.write(safe(r.questionnaireType) + ",");
                    fw.write(safe(r.district) + ",");
                    fw.write(safe(r.respondentName) + ",");
                    fw.write(safe(r.status) + ",");
                    fw.write(r.createdAt > 0 ? sdf.format(new Date(r.createdAt)) : "" );
                    fw.write(",");
                    fw.write(r.submittedAt > 0 ? sdf.format(new Date(r.submittedAt)) : "");
                    fw.write(",");
                    fw.write(safe(r.officerId) + "\n");
                }
                fw.close();
                final File finalFile = csvFile;
                runOnUiThread(() -> {
                    Snackbar.make(recyclerView, "Exported: " + fname, Snackbar.LENGTH_LONG)
                        .setAction("Share", sv -> {
                            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", finalFile);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/csv");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(shareIntent, "Share CSV"));
                        }).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Snackbar.make(recyclerView, "Export failed: " + e.getMessage(), Snackbar.LENGTH_SHORT).show());
            }
        });
    }

    private String safe(String s) { return s != null ? "\"" + s.replace("\"","''") + "\"" : ""; }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
