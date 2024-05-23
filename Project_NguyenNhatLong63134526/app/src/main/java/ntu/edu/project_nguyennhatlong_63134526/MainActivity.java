package ntu.edu.project_nguyennhatlong_63134526;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NotePrefs";
    private static final String KEY_NOTE_COUNT = "NoteCount";
    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;
    private LinearLayout notesContainer;
    private List<Note> noteList;
    private TextView noteCountTextView;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesContainer = findViewById(R.id.notesContainer);
        noteCountTextView = findViewById(R.id.noteCountTextView);
        searchEditText = findViewById(R.id.searchEditText);
        Button addNoteButton = findViewById(R.id.addNoteButton);

        noteList = new ArrayList<>();

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    refreshNoteViews();
                } else {
                    searchNotes();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadNotesFromPreferences();
        displayNotes();
        updateNoteCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK && data != null) {
            String noteTitle = data.getStringExtra("noteTitle");
            String noteContent = data.getStringExtra("noteContent");
            byte[] imageByteArray = data.getByteArrayExtra("image");

            Note note = new Note();
            note.setTitle(noteTitle);
            note.setContent(noteContent);
            note.setTimestamp(System.currentTimeMillis());
            note.setImage(imageByteArray);

            noteList.add(note);
            saveNotesToPreferences();
            refreshNoteViews();
            updateNoteCount();
        } else if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK && data != null) {
            String editedTitle = data.getStringExtra("editedTitle");
            String editedContent = data.getStringExtra("editedContent");
            byte[] editedImage = data.getByteArrayExtra("editedImage");
            int noteIndex = data.getIntExtra("noteIndex", -1);

            if (noteIndex != -1) {
                Note note = noteList.get(noteIndex);
                note.setTitle(editedTitle);
                note.setContent(editedContent);
                note.setTimestamp(System.currentTimeMillis());
                note.setImage(editedImage);

                saveNotesToPreferences();
                refreshNoteViews();
                updateNoteCount();
            }
        }
    }


    private void displayNotes() {
        for (int i = 0; i < noteList.size(); i++) {
            createNoteView(noteList.get(i), i);
        }
    }

    private void loadNotesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int noteCount = sharedPreferences.getInt(KEY_NOTE_COUNT, 0);

        for (int i = 0; i < noteCount; i++) {
            String title = sharedPreferences.getString("note_title_" + i, "");
            String content = sharedPreferences.getString("note_content_" + i, "");
            long timestamp = sharedPreferences.getLong("note_timestamp_" + i, 0);
            String imageBase64 = sharedPreferences.getString("note_image_" + i, "");

            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);
            note.setTimestamp(timestamp);

            if (!imageBase64.isEmpty()) {
                byte[] imageByteArray = Base64.decode(imageBase64, Base64.DEFAULT);
                note.setImage(imageByteArray);
            }

            noteList.add(note);
        }
    }

    private void saveNotesToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_NOTE_COUNT, noteList.size());
        for (int i = 0; i < noteList.size(); i++) {
            Note note = noteList.get(i);
            editor.putString("note_title_" + i, note.getTitle());
            editor.putString("note_content_" + i, note.getContent());
            editor.putLong("note_timestamp_" + i, note.getTimestamp());

            if (note.getImage() != null) {
                String imageBase64 = Base64.encodeToString(note.getImage(), Base64.DEFAULT);
                editor.putString("note_image_" + i, imageBase64);
            } else {
                editor.putString("note_image_" + i, "");
            }
        }
        editor.apply();
    }

    private void createNoteView(final Note note, final int index) {
        View noteView = getLayoutInflater().inflate(R.layout.note_item, null);
        TextView titleTextView = noteView.findViewById(R.id.titleTextView1);
        TextView contentTextView = noteView.findViewById(R.id.contentTextView1);
        TextView timeTextView = noteView.findViewById(R.id.timeTextView1);
        ImageView noteImageView = noteView.findViewById(R.id.noteImageView1);
        Button editButton = noteView.findViewById(R.id.editButton1);

        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(new Date(note.getTimestamp()));
        timeTextView.setText(formattedTime);

        if (note.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(note.getImage(), 0, note.getImage().length);
            noteImageView.setImageBitmap(bitmap);
            noteImageView.setVisibility(View.VISIBLE);
        } else {
            noteImageView.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("content", note.getContent());
                intent.putExtra("image", note.getImage());
                intent.putExtra("noteIndex", index);
                startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
            }
        });

        noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("content", note.getContent());
                intent.putExtra("image", note.getImage());
                startActivity(intent);
            }
        });

        noteView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(note);
                return true;
            }
        });

        notesContainer.addView(noteView);
    }

    private void showDeleteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa ghi chú");
        builder.setMessage("Bạn có chắc chắn muốn xóa ghi chú này không?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteAndRefresh(note);
            }
        });
        builder.setNegativeButton("Quay lại", null);
        builder.show();
    }

    private void deleteNoteAndRefresh(Note note) {
        noteList.remove(note);
        saveNotesToPreferences();
        refreshNoteViews();
        updateNoteCount();
    }

    private void refreshNoteViews() {
        notesContainer.removeAllViews();
        displayNotes();
    }

    private void updateNoteCount() {
        noteCountTextView.setText("Số ghi chú: " + noteList.size());
    }

    private void searchNotes() {
        String keyword = searchEditText.getText().toString().trim().toLowerCase();
        if (!keyword.isEmpty()) {
            List<Note> filteredNotes = new ArrayList<>();
            for (Note note : noteList) {
                if (note.getTitle().toLowerCase().contains(keyword)) {
                    filteredNotes.add(note);
                }
            }
            displayFilteredNotes(filteredNotes);
        } else {
            refreshNoteViews();
        }
    }

    private void displayFilteredNotes(List<Note> filteredNotes) {
        notesContainer.removeAllViews();
        for (Note note : filteredNotes) {
            createNoteView(note, noteList.indexOf(note));
        }
    }
}