package me.trashout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;

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
                        Utils.resizeBitmapToSquare(file);
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent();
                        intent.setData(uri);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });

        // crop image preview
        LinearLayout camLayout = findViewById(R.id.camLayout);
        ViewGroup.LayoutParams params = camLayout.getLayoutParams();
        int size = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.width = size;
        params.height = size;
        camLayout.setLayoutParams(params);
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