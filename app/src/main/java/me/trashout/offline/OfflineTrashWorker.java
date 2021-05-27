package me.trashout.offline;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class OfflineTrashWorker extends Worker {
    public OfflineTrashWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        OfflineTrashManager offlineTrashManager = new OfflineTrashManager(getApplicationContext());
        offlineTrashManager.processAll();

        return Result.success();
    }
}
