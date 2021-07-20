package me.trashout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.trashout.utils.Utils;

public class PhotoActivity extends AppCompatActivity {
    private static final int CONFIRM = 34;

    @BindView(R.id.camera)
    CameraView camera;

    @BindView(R.id.flash)
    ImageButton flash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ButterKnife.bind(this);

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

                        Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
                        intent.setData(uri);
                        startActivityForResult(intent, CONFIRM);
                    }
                });
            }
        });

        // crop image preview
        View camLayout = findViewById(R.id.camVis);
        ViewGroup.LayoutParams params = camLayout.getLayoutParams();
        int size = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.width = size;
        params.height = size;
        camLayout.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONFIRM && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.setData(data.getData());
            setResult(RESULT_OK, intent);
            finish();
        }
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

    public void flash(View view) {
        switch(flash.getTag().toString()) {
            case "auto":
                camera.setFlash(Flash.ON);
                flash.setImageResource(R.drawable.flash_on);
                flash.setTag("on");
                break;

            case "on":
                camera.setFlash(Flash.OFF);
                flash.setImageResource(R.drawable.flash_off);
                flash.setTag("off");
                break;

            case "off":
                camera.setFlash(Flash.AUTO);
                flash.setImageResource(R.drawable.flash_auto);
                flash.setTag("auto");
                break;
        }
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