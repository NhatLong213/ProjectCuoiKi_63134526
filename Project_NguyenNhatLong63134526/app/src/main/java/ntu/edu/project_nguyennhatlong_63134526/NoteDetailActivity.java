package ntu.edu.project_nguyennhatlong_63134526;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView contentTextView;
    private ImageView noteImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        titleTextView = findViewById(R.id.detailTitleTextView);
        contentTextView = findViewById(R.id.detailContentTextView);
        noteImageView = findViewById(R.id.detailImageView);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        byte[] image = getIntent().getByteArrayExtra("image");

        titleTextView.setText(title);
        contentTextView.setText(content);

        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            noteImageView.setImageBitmap(bitmap);
            noteImageView.setVisibility(View.VISIBLE);
        } else {
            noteImageView.setVisibility(View.GONE);
        }
    }
}
