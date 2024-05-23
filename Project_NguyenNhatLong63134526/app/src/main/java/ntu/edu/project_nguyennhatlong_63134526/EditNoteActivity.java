package ntu.edu.project_nguyennhatlong_63134526;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditNoteActivity extends Activity {

    private EditText titleEditText;
    private EditText contentEditText;
    private ImageView noteImageView;
    private Button saveButton;
    private Button changeImageButton;
    private Button deleteImageButton;
    private byte[] currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        noteImageView = findViewById(R.id.noteImageView);
        saveButton = findViewById(R.id.saveButton);
        changeImageButton = findViewById(R.id.changeImageButton);
        deleteImageButton = findViewById(R.id.deleteImageButton);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        byte[] image = intent.getByteArrayExtra("image");

        titleEditText.setText(title);
        contentEditText.setText(content);

        if (image != null && image.length > 0) {
            currentImage = image; // Lưu lại hình ảnh hiện tại
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            noteImageView.setImageBitmap(bitmap);
            noteImageView.setVisibility(View.VISIBLE);
            deleteImageButton.setVisibility(View.VISIBLE);
        } else {
            noteImageView.setVisibility(View.GONE);
            deleteImageButton.setVisibility(View.GONE);
        }

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở hoạt động để chọn hình ảnh mới (implement chọn hình ảnh)
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImage = null; // Xóa hình ảnh hiện tại
                noteImageView.setVisibility(View.GONE);
                deleteImageButton.setVisibility(View.GONE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("editedTitle", titleEditText.getText().toString());
                data.putExtra("editedContent", contentEditText.getText().toString());
                data.putExtra("editedImage", currentImage); // Truyền lại hình ảnh đã chỉnh sửa hoặc hình ảnh cũ
                int noteIndex = getIntent().getIntExtra("noteIndex", -1);
                data.putExtra("noteIndex", noteIndex);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Xử lý hình ảnh được chọn
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 800); // Giảm kích thước ảnh nếu cần thiết
                noteImageView.setImageBitmap(resizedBitmap);
                noteImageView.setVisibility(View.VISIBLE);

                // Convert Bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                currentImage = stream.toByteArray();

                // Hiển thị nút "Xóa hình ảnh"
                deleteImageButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        if (ratioMax > 1) {
            finalWidth = (int) ((float)maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float)maxWidth / ratioBitmap);
        }
        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }
}
