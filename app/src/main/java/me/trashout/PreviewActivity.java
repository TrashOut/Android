package me.trashout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {
    @BindView(R.id.photo)
    ImageView photo;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ButterKnife.bind(this);

        uri = getIntent().getData();
        photo.setImageURI(uri);
    }

    public void onOk(View view) {
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onRepeat(View view) {
        finish();
    }
}