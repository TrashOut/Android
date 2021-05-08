package me.trashout.service.offline;

import com.google.gson.Gson;

import java.util.ArrayList;

class OfflineTrashList {
    public ArrayList<OfflineTrash> list = new ArrayList<>();

    public String serialize() {
        return new Gson().toJson(this);
    }

    static public OfflineTrashList create(String serializedData) {
        return new Gson().fromJson(serializedData, OfflineTrashList.class);
    }
}
