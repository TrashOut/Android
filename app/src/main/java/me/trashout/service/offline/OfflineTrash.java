package me.trashout.service.offline;

import android.net.Uri;

import java.util.ArrayList;

import me.trashout.model.Trash;

public class OfflineTrash {
    private boolean update;
    private Trash trash;
    private ArrayList<String> photos; // using ArrayList<String> instead of original ArrayList<Uri>, because GSON has problem with that

    public OfflineTrash(Trash trash, ArrayList<Uri> photos, boolean update) {
        setTrash(trash);
        setPhotos(photos);
        isUpdate(update);
    }

    public boolean isUpdate() {
        return update;
    }

    public void isUpdate(boolean update) {
        this.update = update;
    }

    public Trash getTrash() {
        return trash;
    }

    public void setTrash(Trash trash) {
        this.trash = trash;
    }

    public ArrayList<Uri> getPhotos() {
        ArrayList<Uri> photosUri = new ArrayList<Uri>();

        for (String uriStr : photos) {
            photosUri.add(Uri.parse(uriStr));
        }

        return photosUri;
    }

    public void setPhotos(ArrayList<Uri> photos) {
        this.photos = new ArrayList<String>();

        for (Uri uri : photos) {
            this.photos.add(uri.toString());
        }
    }
}
