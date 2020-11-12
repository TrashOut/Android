package me.trashout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Date;

import me.trashout.utils.Utils;

public class PhotoActivity extends AppCompatActivity {
    CameraView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        camera = findViewById(R.id.camera);

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                Date date = new Date();
                final String filename = getApplicationContext().getCacheDir().getPath() + "/img_" + date.getTime() + "." + result.getFormat().toString().toLowerCase();
                result.toFile(new File(filename), new FileCallback() {
                    @Override
                    public void onFileReady(File file) {
                        Utils.resizeBitmap(file);
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent();
                        intent.setData(uri);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void take(View view) {
        camera.takePicture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }
}