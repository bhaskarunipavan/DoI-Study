package com.dic.survey.sync;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.dic.survey.utils.NetworkUtils;
import java.util.concurrent.CountDownLatch;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!NetworkUtils.isOnline(getApplicationContext())) {
            return Result.retry();
        }

        final Result[] result = {Result.success()};
        CountDownLatch latch = new CountDownLatch(1);

        SyncManager syncManager = new SyncManager(getApplicationContext());
        syncManager.syncPending(new SyncManager.SyncCallback() {
            @Override
            public void onSyncComplete(int synced, int failed) {
                result[0] = failed > 0 ? Result.retry() : Result.success();
                latch.countDown();
            }
            @Override
            public void onSyncError(String error) {
                result[0] = Result.retry();
                latch.countDown();
            }
        });

        try { latch.await(60, java.util.concurrent.TimeUnit.SECONDS); }
        catch (InterruptedException e) { return Result.retry(); }
        return result[0];
    }
}
