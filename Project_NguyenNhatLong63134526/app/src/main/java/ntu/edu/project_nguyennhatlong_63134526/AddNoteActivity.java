package ntu.edu.project_nguyennhatlong_63134526;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddNoteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private Button chooseImageButton;
    private ImageView noteImageView;
    private byte[] imageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        noteImageView = findViewById(R.id.noteImageView);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_CODE_PICK_IMAGE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTitle = titleEditText.getText().toString();
                String noteContent = contentEditText.getText().toString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("noteTitle", noteTitle);
                resultIntent.putExtra("noteContent", noteContent);
                resultIntent.putExtra("image", imageByteArray);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                // Giảm kích thước hình ảnh
                Bitmap resizedBitmap = getResizedBitmap(bitmap, 1024);  // maxSize là kích thước tối đa bạn muốn

                noteImageView.setImageBitmap(resizedBitmap);
                noteImageView.setVisibility(View.VISIBLE);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageByteArray = byteArrayOutputStream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
