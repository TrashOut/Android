package me.trashout.service.offline;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;

import me.trashout.model.Trash;
import me.trashout.service.CreateTrashService;
import me.trashout.service.UpdateTrashService;

public class OfflineTrashManager {
    private Context context;
    private OfflineTrashList offlineTrashList = new OfflineTrashList();
    private SharedPreferences sharedPreferences;

    private static final int CREATE_TRASH_REQUEST_ID = 450;
    private static final int UPDATE_TRASH_REQUEST_ID = 451;
    private static final String PREFS_NAME = "Offline";
    private static final String PREFS_KEY = "TrashList";

    public OfflineTrashManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String serializedDataFromPreference = sharedPreferences.getString(PREFS_KEY, null);

        if (serializedDataFromPreference != null) {
            this.offlineTrashList = OfflineTrashList.create(serializedDataFromPreference);
        }
    }

    public void add(Trash trash, ArrayList<Uri> photos, boolean update) {
        offlineTrashList.list.add(new OfflineTrash(trash, photos, update));
        save();
    }

    public OfflineTrash get() {
        return offlineTrashList.list.size() > 0 ? offlineTrashList.list.get(0) : null;
    }

    public void remove() {
        if (offlineTrashList.list.size() > 0) {
            offlineTrashList.list.remove(0);
            save();
        }
    }

    private void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFS_KEY, offlineTrashList.serialize());
        editor.commit();
    }

    public boolean process() {
        OfflineTrash offlineTrash = get();

        if (offlineTrash == null) {
            return false;
        }

        if (offlineTrash.isUpdate()) {
            UpdateTrashService.startForRequest(context, UPDATE_TRASH_REQUEST_ID, offlineTrash.getTrash().getId(), offlineTrash.getTrash(), offlineTrash.getPhotos());
        } else {
            CreateTrashService.startForRequest(context, CREATE_TRASH_REQUEST_ID, offlineTrash.getTrash(), offlineTrash.getPhotos());
        }

        remove();
        return true;
    }
}
