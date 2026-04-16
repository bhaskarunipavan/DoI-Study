package com.dic.survey.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.dic.survey.R;
import com.dic.survey.database.SurveyDatabase;
import com.dic.survey.models.SurveyResponse;
import com.dic.survey.utils.PrefManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public class MISReportActivity extends AppCompatActivity {

    private LinearLayout container;
    private SurveyDatabase db;
    private PrefManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_report);
        prefs = new PrefManager(this);
        db = SurveyDatabase.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MIS Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        container = findViewById(R.id.misContainer);
        loadReports();
        findViewById(R.id.btnRefresh).setOnClickListener(v -> loadReports());
    }

    private void loadReports() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<SurveyResponse> all = db.surveyDao().getAllResponsesSync();
            runOnUiThread(() -> buildReports(all));
        });
    }

    private void buildReports(List<SurveyResponse> all) {
        container.removeAllViews();
        int total = all.size();
        int synced = 0, submitted = 0, drafts = 0;
        int dicCount = 0, enterpriseCount = 0, assocCount = 0, stakeCount = 0;
        Map<String, Integer> districtMap = new HashMap<>();
        Map<String, Integer> officerMap = new HashMap<>();
        long today = System.currentTimeMillis();
        long dayMs = 86400000L;
        int last7days = 0, last30days = 0;

        for (SurveyResponse r : all) {
            if (SurveyResponse.STATUS_SYNCED.equals(r.status)) synced++;
            else if (SurveyResponse.STATUS_SUBMITTED.equals(r.status)) submitted++;
            else drafts++;
            if (SurveyResponse.TYPE_DIC.equals(r.questionnaireType)) dicCount++;
            else if (SurveyResponse.TYPE_ENTERPRISE.equals(r.questionnaireType)) enterpriseCount++;
            else if (SurveyResponse.TYPE_ASSOCIATION.equals(r.questionnaireType)) assocCount++;
            else if (SurveyResponse.TYPE_STAKEHOLDER.equals(r.questionnaireType)) stakeCount++;
            if (r.district != null && !r.district.isEmpty())
                districtMap.put(r.district, districtMap.getOrDefault(r.district, 0) + 1);
            if (r.officerId != null && !r.officerId.isEmpty())
                officerMap.put(r.officerId, officerMap.getOrDefault(r.officerId, 0) + 1);
            long ts = r.submittedAt > 0 ? r.submittedAt : r.createdAt;
            if (today - ts <= 7 * dayMs) last7days++;
            if (today - ts <= 30 * dayMs) last30days++;
        }

        addSectionHeader("Summary Overview");
        addStatRow("Total Responses", String.valueOf(total), "#1A4B8C",
                   "Synced to Server", String.valueOf(synced), "#4CAF50");
        addStatRow("Submitted (Pending Sync)", String.valueOf(submitted), "#2196F3",
                   "Drafts", String.valueOf(drafts), "#FF9800");
        addStatRow("Last 7 Days", String.valueOf(last7days), "#9C27B0",
                   "Last 30 Days", String.valueOf(last30days), "#00BCD4");

        addSectionHeader("Responses by Questionnaire Type");
        int[] counts = {dicCount, enterpriseCount, assocCount, stakeCount};
        String[] labels = {"DIC / DoI", "Enterprise", "Association", "Stakeholder"};
        String[] icons = {"\uD83C\uDFE2", "\uD83C\uDFED", "\uD83E\uDD1D", "\uD83C\uDFDB"};
        int[] colors = {Color.parseColor("#1565C0"), Color.parseColor("#2E7D32"),
                        Color.parseColor("#E65100"), Color.parseColor("#6A1B9A")};
        addBarChart(labels, icons, counts, colors, total);

        addSectionHeader("Completion Rate");
        addProgressCard("Submitted + Synced", synced + submitted, total, "#4CAF50");
        addProgressCard("Synced to Server", synced, total, "#1A4B8C");
        addProgressCard("Drafts (Incomplete)", drafts, total, "#FF9800");

        if (!districtMap.isEmpty()) {
            addSectionHeader("Top Districts by Response Count");
            districtMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue()).limit(10)
                .forEach(e -> addDistrictRow(e.getKey(), e.getValue(), total));
        }
        if (!officerMap.isEmpty()) {
            addSectionHeader("Responses by Officer");
            officerMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> addOfficerRow(e.getKey(), e.getValue()));
        }

        addSectionHeader("Sync Status Summary");
        addSyncStatusTable(synced, submitted, drafts, total);

        addSectionHeader("Recent Activity (Last 10)");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        all.stream()
            .sorted((a, b) -> Long.compare(
                b.updatedAt > 0 ? b.updatedAt : b.createdAt,
                a.updatedAt > 0 ? a.updatedAt : a.createdAt))
            .limit(10)
            .forEach(r -> {
                long ts = r.updatedAt > 0 ? r.updatedAt : r.createdAt;
                addActivityRow(typeLabel(r.questionnaireType),
                    r.district != null ? r.district : "--",
                    r.status != null ? r.status : "DRAFT",
                    ts > 0 ? sdf.format(new Date(ts)) : "--");
            });

        TextView footer = new TextView(this);
        footer.setText("Report generated: " + new SimpleDateFormat(
            "dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()));
        footer.setTextSize(11f);
        footer.setTextColor(Color.parseColor("#9E9E9E"));
        footer.setPadding(0, dp(24), 0, dp(24));
        footer.setGravity(android.view.Gravity.CENTER);
        container.addView(footer);
    }

    private void addSectionHeader(String title) {
        TextView tv = new TextView(this);
        tv.setText(title); tv.setTextSize(15f);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#1A4B8C"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(20), 0, dp(8)); tv.setLayoutParams(lp);
        container.addView(tv);
        View div = new View(this);
        div.setBackgroundColor(Color.parseColor("#1A4B8C")); div.setAlpha(0.2f);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(-1, dp(1));
        dlp.setMargins(0, 0, 0, dp(10)); div.setLayoutParams(dlp);
        container.addView(div);
    }

    private void addStatRow(String l1, String v1, String c1, String l2, String v2, String c2) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, -2);
        rlp.setMargins(0, 0, 0, dp(10)); row.setLayoutParams(rlp);
        row.addView(makeStatCard(l1, v1, c1));
        View sp = new View(this); sp.setLayoutParams(new LinearLayout.LayoutParams(dp(10), -1));
        row.addView(sp); row.addView(makeStatCard(l2, v2, c2));
        container.addView(row);
    }

    private CardView makeStatCard(String label, String value, String colorHex) {
        CardView card = new CardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        card.setRadius(dp(10)); card.setCardElevation(dp(3));
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setBackgroundColor(Color.parseColor(colorHex));
        inner.setPadding(dp(14), dp(14), dp(14), dp(14));
        TextView tvVal = new TextView(this);
        tvVal.setText(value); tvVal.setTextSize(28f);
        tvVal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvVal.setTextColor(Color.WHITE);
        TextView tvLbl = new TextView(this);
        tvLbl.setText(label); tvLbl.setTextSize(11f);
        tvLbl.setTextColor(Color.argb(200, 255, 255, 255));
        inner.addView(tvVal); inner.addView(tvLbl); card.addView(inner);
        return card;
    }

    private void addBarChart(String[] labels, String[] icons, int[] counts, int[] colors, int total) {
        CardView card = new CardView(this);
        card.setRadius(dp(10)); card.setCardElevation(dp(2));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 0, 0, dp(10)); card.setLayoutParams(clp);
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(16), dp(14), dp(16), dp(14));
        inner.setBackgroundColor(Color.WHITE);
        for (int i = 0; i < labels.length; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, -2);
            rlp.setMargins(0, dp(6), 0, dp(6)); row.setLayoutParams(rlp);
            TextView icon = new TextView(this); icon.setText(icons[i]); icon.setTextSize(16f);
            icon.setLayoutParams(new LinearLayout.LayoutParams(dp(30), -2));
            TextView lbl = new TextView(this); lbl.setText(labels[i]); lbl.setTextSize(13f);
            lbl.setTextColor(Color.parseColor("#424242"));
            lbl.setLayoutParams(new LinearLayout.LayoutParams(dp(110), -2));
            LinearLayout barBg = new LinearLayout(this);
            barBg.setBackgroundColor(Color.parseColor("#F5F5F5"));
            barBg.setLayoutParams(new LinearLayout.LayoutParams(0, dp(18), 1f));
            View bar = new View(this);
            int pct = total > 0 ? (counts[i] * 100 / total) : 0;
            bar.setLayoutParams(new LinearLayout.LayoutParams(0, -1, pct > 0 ? pct : 0));
            bar.setBackgroundColor(colors[i]); barBg.addView(bar);
            TextView cnt = new TextView(this); cnt.setText("  " + counts[i]);
            cnt.setTextSize(13f); cnt.setTypeface(null, android.graphics.Typeface.BOLD);
            cnt.setTextColor(colors[i]);
            cnt.setLayoutParams(new LinearLayout.LayoutParams(dp(36), -2));
            row.addView(icon); row.addView(lbl); row.addView(barBg); row.addView(cnt);
            inner.addView(row);
        }
        card.addView(inner); container.addView(card);
    }

    private void addProgressCard(String label, int value, int total, String colorHex) {
        int pct = total > 0 ? (value * 100 / total) : 0;
        CardView card = new CardView(this); card.setRadius(dp(8)); card.setCardElevation(dp(2));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 0, 0, dp(8)); card.setLayoutParams(clp);
        LinearLayout inner = new LinearLayout(this); inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(14), dp(12), dp(14), dp(12)); inner.setBackgroundColor(Color.WHITE);
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        TextView lbl = new TextView(this); lbl.setText(label); lbl.setTextSize(13f);
        lbl.setTextColor(Color.parseColor("#424242"));
        lbl.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        TextView pctTv = new TextView(this); pctTv.setText(value + "  (" + pct + "%)");
        pctTv.setTextSize(13f); pctTv.setTypeface(null, android.graphics.Typeface.BOLD);
        pctTv.setTextColor(Color.parseColor(colorHex));
        row.addView(lbl); row.addView(pctTv); inner.addView(row);
        LinearLayout barBg = new LinearLayout(this);
        barBg.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-1, dp(8));
        blp.setMargins(0, dp(6), 0, 0); barBg.setLayoutParams(blp);
        View bar = new View(this); bar.setBackgroundColor(Color.parseColor(colorHex));
        bar.setLayoutParams(new LinearLayout.LayoutParams(0, -1, pct > 0 ? pct : 0));
        barBg.addView(bar);
        View rem = new View(this);
        rem.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 100 - pct));
        barBg.addView(rem); inner.addView(barBg); card.addView(inner); container.addView(card);
    }

    private void addDistrictRow(String district, int count, int total) {
        int pct = total > 0 ? (count * 100 / total) : 0;
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, -2);
        rlp.setMargins(0, dp(4), 0, dp(4)); row.setLayoutParams(rlp);
        TextView lbl = new TextView(this); lbl.setText(district); lbl.setTextSize(13f);
        lbl.setTextColor(Color.parseColor("#424242"));
        lbl.setLayoutParams(new LinearLayout.LayoutParams(dp(130), -2));
        LinearLayout barBg = new LinearLayout(this);
        barBg.setBackgroundColor(Color.parseColor("#E3F2FD"));
        barBg.setLayoutParams(new LinearLayout.LayoutParams(0, dp(14), 1f));
        View bar = new View(this); bar.setBackgroundColor(Color.parseColor("#1A4B8C"));
        bar.setLayoutParams(new LinearLayout.LayoutParams(0, -1, pct > 0 ? pct : 0));
        barBg.addView(bar);
        TextView cnt = new TextView(this); cnt.setText("  " + count); cnt.setTextSize(13f);
        cnt.setTypeface(null, android.graphics.Typeface.BOLD);
        cnt.setTextColor(Color.parseColor("#1A4B8C"));
        cnt.setLayoutParams(new LinearLayout.LayoutParams(dp(30), -2));
        row.addView(lbl); row.addView(barBg); row.addView(cnt); container.addView(row);
    }

    private void addOfficerRow(String officer, int count) {
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, -2);
        rlp.setMargins(0, dp(3), 0, dp(3)); row.setLayoutParams(rlp);
        TextView icon = new TextView(this); icon.setText("\uD83D\uDC64"); icon.setTextSize(14f);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(28), -2));
        TextView lbl = new TextView(this); lbl.setText(officer); lbl.setTextSize(13f);
        lbl.setTextColor(Color.parseColor("#424242"));
        lbl.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        TextView cnt = new TextView(this); cnt.setText(count + " responses"); cnt.setTextSize(12f);
        cnt.setTextColor(Color.parseColor("#1A4B8C"));
        cnt.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(icon); row.addView(lbl); row.addView(cnt); container.addView(row);
    }

    private void addSyncStatusTable(int synced, int submitted, int drafts, int total) {
        CardView card = new CardView(this); card.setRadius(dp(10)); card.setCardElevation(dp(2));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 0, 0, dp(10)); card.setLayoutParams(clp);
        LinearLayout table = new LinearLayout(this); table.setOrientation(LinearLayout.VERTICAL);
        table.setPadding(dp(16), dp(14), dp(16), dp(14)); table.setBackgroundColor(Color.WHITE);
        addTableRow(table, "Status", "Count", "% of Total", true);
        addTableRow(table, "Synced to Server", String.valueOf(synced), pct(synced,total)+"%", false);
        addTableRow(table, "Submitted (Pending)", String.valueOf(submitted), pct(submitted,total)+"%", false);
        addTableRow(table, "Drafts", String.valueOf(drafts), pct(drafts,total)+"%", false);
        addTableRow(table, "TOTAL", String.valueOf(total), "100%", false);
        card.addView(table); container.addView(card);
    }

    private void addTableRow(LinearLayout parent, String c1, String c2, String c3, boolean hdr) {
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(8), 0, dp(8));
        if (hdr) row.setBackgroundColor(Color.parseColor("#E3F2FD"));
        int color = hdr ? Color.parseColor("#1A4B8C") : Color.parseColor("#424242");
        int style = hdr ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL;
        TextView t1 = new TextView(this); t1.setText(c1); t1.setTextSize(13f);
        t1.setTextColor(color); t1.setTypeface(null, style);
        t1.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 2f)); t1.setPadding(dp(6),0,0,0);
        TextView t2 = new TextView(this); t2.setText(c2); t2.setTextSize(13f);
        t2.setTextColor(color); t2.setTypeface(null, style);
        t2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        t2.setGravity(android.view.Gravity.CENTER);
        TextView t3 = new TextView(this); t3.setText(c3); t3.setTextSize(13f);
        t3.setTextColor(color); t3.setTypeface(null, style);
        t3.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        t3.setGravity(android.view.Gravity.END); t3.setPadding(0,0,dp(6),0);
        row.addView(t1); row.addView(t2); row.addView(t3); parent.addView(row);
        View div = new View(this); div.setBackgroundColor(Color.parseColor("#E0E0E0"));
        div.setLayoutParams(new LinearLayout.LayoutParams(-1, 1)); parent.addView(div);
    }

    private void addActivityRow(String type, String district, String status, String date) {
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, -2);
        rlp.setMargins(0, dp(4), 0, dp(4)); row.setLayoutParams(rlp);
        row.setPadding(0, dp(6), 0, dp(6));
        TextView lbl = new TextView(this); lbl.setText(type); lbl.setTextSize(12f);
        lbl.setTextColor(Color.parseColor("#424242"));
        lbl.setLayoutParams(new LinearLayout.LayoutParams(dp(90), -2));
        TextView dist = new TextView(this); dist.setText(district); dist.setTextSize(12f);
        dist.setTextColor(Color.parseColor("#757575"));
        dist.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        TextView badge = new TextView(this); badge.setText(status); badge.setTextSize(10f);
        badge.setTextColor(Color.WHITE); badge.setPadding(dp(6),dp(2),dp(6),dp(2));
        int bc; switch (status) {
            case "SYNCED": bc = Color.parseColor("#4CAF50"); break;
            case "SUBMITTED": bc = Color.parseColor("#2196F3"); break;
            default: bc = Color.parseColor("#FF9800");
        }
        badge.setBackgroundColor(bc);
        TextView dateTv = new TextView(this); dateTv.setText("  " + date); dateTv.setTextSize(10f);
        dateTv.setTextColor(Color.parseColor("#9E9E9E"));
        dateTv.setLayoutParams(new LinearLayout.LayoutParams(dp(100), -2));
        row.addView(lbl); row.addView(dist); row.addView(badge); row.addView(dateTv);
        container.addView(row);
        View div = new View(this); div.setBackgroundColor(Color.parseColor("#F0F0F0"));
        div.setLayoutParams(new LinearLayout.LayoutParams(-1, 1)); container.addView(div);
    }

    private String typeLabel(String type) {
        if (type == null) return "Unknown";
        switch (type) {
            case "DIC": return "DIC/DoI"; case "ENTERPRISE": return "Enterprise";
            case "ASSOCIATION": return "Association"; case "STAKEHOLDER": return "Stakeholder";
            default: return type;
        }
    }
    private int pct(int val, int total) { return total > 0 ? (val * 100 / total) : 0; }
    private int dp(int val) { return (int)(val * getResources().getDisplayMetrics().density); }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
