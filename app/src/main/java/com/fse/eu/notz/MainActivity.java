package com.fse.eu.notz;


        import android.app.Activity;
        import android.content.ContentValues;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.StaggeredGridLayoutManager;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ProgressBar;

        import java.util.ArrayList;

        import eu.fse.notz.R;

public class MainActivity extends AppCompatActivity {

    public static final int EDIT_REQUEST = 1001;
    public static final int RESUL_REMOVE_NOTE = RESULT_FIRST_USER + 1;

    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton addNoteButton;
    private ProgressBar loading;
    private ImageView favourite;
    private DatabaseHandler mDbHandler;
    private CheckBox favourite_cb;

    // private String[] myDataset = {"nota 1"," nota 2", "fai la spesa", "paga bolletta luca", "dadsadasa", "dsasdasd", "dassad"};
    private ArrayList<Note> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.notes_rv);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        favourite_cb= (CheckBox) findViewById(R.id.checkBox) ;

        loading.setVisibility(View.VISIBLE);

        addNoteButton = (FloatingActionButton) findViewById(R.id.add_note_fab);

        mLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();

        mDbHandler = new DatabaseHandler(this);
        myDataset.addAll(mDbHandler.getAllNotes());


        // specify an adapter (see also next example)
        mAdapter = new NotesAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        loading.setVisibility(View.INVISIBLE);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();

            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_REQUEST) {

            if (resultCode == RESULT_OK) {

                //getPosition from returnIntent
                int editedNotePosition = data.getIntExtra("position", -1);

                mAdapter.updateNote(editedNotePosition,
                        data.getStringExtra("title"),
                        data.getStringExtra("description"));

                mDbHandler.updateNote(mAdapter.getNote(editedNotePosition));  //IMPORTANTE: QUESTO VA DOPO DEL  mAdapter.updateNote(editedNotePosition ...)

                if(favourite_cb.isChecked()) {
                    Note.NoteBuilder builder = new Note.NoteBuilder();
                    builder
                            .setShownOnTop(true);

                    mAdapter.updateNote(editedNotePosition,data.getStringExtra("title"),
                            data.getStringExtra("description"));
                    mDbHandler.updateNote(mAdapter.getNote(editedNotePosition));

                    favourite = (ImageView) findViewById(R.id.favourite_star);
                    favourite.setVisibility(View.VISIBLE);

                }



            }

            if(resultCode == RESUL_REMOVE_NOTE){
                final int editedNotePosition = data.getIntExtra("position", -1);
                mDbHandler.deletNote(mAdapter.getNote(editedNotePosition)); //IMPORTANTE: QUESTO VA PRIMA DEL  mAdapter.removeNote(editedNotePosition)
                mAdapter.removeNote(editedNotePosition);




                Snackbar.make(mRecyclerView,getString(R.string.note_removed),Snackbar.LENGTH_LONG)
                        .setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Note note = new Note(data.getStringExtra("title"),
                                        data.getStringExtra("description"));

                                mAdapter.addNote(editedNotePosition,note);
                                mDbHandler.addNote(note);
                            }
                        })
                        .show();
            }





        }

    }

    private void showDialog() {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_note, null);


        final EditText titleEt = (EditText) dialogView.findViewById(R.id.dialog_title_et);
        final EditText descriptionEt = (EditText) dialogView.findViewById(R.id.dialog_description_et);


        alertBuilder.setView(dialogView)
                .setTitle(R.string.dialog_add_note_title)
                .setPositiveButton(R.string.dialog_positive_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //

                                String insertedTitle = titleEt.getText().toString();
                                String insertedDescription = descriptionEt.getText().toString();

                                Note.NoteBuilder builder = new Note.NoteBuilder();
                                builder
                                        .setTitle(insertedTitle)
                                        .setDescription(insertedDescription)
                                        .setId(12)
                                        .setShownOnTop(true);

                                mAdapter.addNote(builder.build());
                                mDbHandler.addNote(builder.build());

                            }
                        })
                .setNegativeButton(R.string.dialog_negative_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //
                            }
                        })
                .create()
                .show();

    }



}