package com.dic.survey.sync;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.dic.survey.database.SurveyDatabase;
import com.dic.survey.models.SurveyResponse;
import com.dic.survey.utils.PrefManager;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncManager {
    private static final String TAG = "SyncManager";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final Context context;
    private final SurveyDatabase db;
    private final PrefManager prefs;
    private final OkHttpClient client;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface SyncCallback {
        void onSyncComplete(int synced, int failed);
        void onSyncError(String error);
    }

    public SyncManager(Context context) {
        this.context = context.getApplicationContext();
        this.db = SurveyDatabase.getInstance(this.context);
        this.prefs = new PrefManager(this.context);
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void syncPending(SyncCallback callback) {
        executor.execute(() -> {
            String supabaseUrl = prefs.getSupabaseUrl();
            String supabaseKey = prefs.getSupabaseKey();

            if (supabaseUrl.isEmpty() || supabaseKey.isEmpty()) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onSyncError("Supabase not configured");
                });
                return;
            }

            List<SurveyResponse> pending = db.surveyDao().getPendingSync();
            int synced = 0, failed = 0;

            for (SurveyResponse resp : pending) {
                try {
                    JSONObject body = new JSONObject();
                    body.put("questionnaire_type", resp.questionnaireType);
                    body.put("district", resp.district != null ? resp.district : "");
                    body.put("respondent_name", resp.respondentName != null ? resp.respondentName : "");
                    body.put("form_data", resp.formData != null ? resp.formData : "{}");
                    body.put("status", resp.status);
                    body.put("officer_id", resp.officerId != null ? resp.officerId : "");
                    body.put("created_at", resp.createdAt);
                    body.put("submitted_at", resp.submittedAt);

                    RequestBody reqBody = RequestBody.create(body.toString(), JSON);
                    Request request = new Request.Builder()
                        .url(supabaseUrl + "/rest/v1/survey_responses")
                        .addHeader("apikey", supabaseKey)
                        .addHeader("Authorization", "Bearer " + supabaseKey)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .post(reqBody)
                        .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseBody = response.body().string();
                            // Parse server ID from response
                            String serverId = parseServerId(responseBody);
                            db.surveyDao().markSynced(resp.id, serverId, System.currentTimeMillis());
                            synced++;
                        } else {
                            resp.syncAttempts++;
                            db.surveyDao().update(resp);
                            failed++;
                            Log.w(TAG, "Sync failed for id=" + resp.id + " code=" + response.code());
                        }
                    }
                } catch (Exception e) {
                    failed++;
                    resp.syncAttempts++;
                    db.surveyDao().update(resp);
                    Log.e(TAG, "Sync error for id=" + resp.id, e);
                }
            }

            final int finalSynced = synced, finalFailed = failed;
            if (finalSynced > 0) prefs.setLastSync(System.currentTimeMillis());
            mainHandler.post(() -> {
                if (callback != null) callback.onSyncComplete(finalSynced, finalFailed);
            });
        });
    }

    private String parseServerId(String json) {
        try {
            // Response is a JSON array: [{"id":"..."}]
            if (json.startsWith("[")) {
                JSONObject obj = new JSONObject(json.substring(1, json.length() - 1).trim());
                return obj.optString("id", "");
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public int getPendingCount() {
        return db.surveyDao().getPendingSyncCountSync();
    }
}
