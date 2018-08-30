package com.example.android.vestigeapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.vestigeapp.database.VestigeDatabase;
import com.example.android.vestigeapp.database.VestigeEntry;

import java.util.Date;

public class AddEntry extends AppCompatActivity {
    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";
    // Extra for the entry ID to be received after rotation
    public static final String INSTANCE_ENTRY_ID = "instanceEntryId";
    // Constant for default entry id to be used when not in update mode
    private static final int DEFAULT_ENTRY_ID = -1;

    //View Controls
    private EditText textDescription;
    //private Button saveButton;
    private VestigeDatabase entryDb;
    private int vestigeEntryId = DEFAULT_ENTRY_ID;
    private Date originalCreatedDate = new Date();

    //Status codes for the priority
    public static final int STATUS_URGENT = 1;
    public static final int STATUS_IMPORTANT = 2;
    public static final int STATUS_NORMAL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        initViews();
        entryDb = VestigeDatabase.getInstance(getApplicationContext());
        //manage instance state
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            vestigeEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)) {
           // saveButton.setText(R.string.btn_update);
            if (vestigeEntryId == DEFAULT_ENTRY_ID) {
                // populate the UI
                vestigeEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);

                final LiveData<VestigeEntry> entry = entryDb.vestigeDAO().loadEntryById(vestigeEntryId);
                //originalCreatedDate = entry.getValue().getCreatedOn();
                entry.observe(this, new Observer<VestigeEntry>() {
                    @Override
                    public void onChanged(@Nullable VestigeEntry vestigeEntry) {
                        updateUIFromEntry(vestigeEntry);
                    }
                });

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.note_update) {
            saveButtonClicked();
        }
        //finish();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(INSTANCE_ENTRY_ID, vestigeEntryId);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initViews(){

        textDescription = (EditText)findViewById(R.id.entry_description);
        /*saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClicked();
            }
        });*/
        entryDb = VestigeDatabase.getInstance(getApplicationContext());
    }

    private void saveButtonClicked(){
        final String description = textDescription.getText().toString();
        int priority = getStatusFromRadioButton();
        Date entryDate = new Date();
        if(description.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter a quick note", Toast.LENGTH_LONG).show();

        }else {
            final VestigeEntry _entry = new VestigeEntry(description, priority, entryDate, entryDate);
            VestigeThreadExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {


                    if (vestigeEntryId == DEFAULT_ENTRY_ID) {
                        // insert new entry
                        entryDb.vestigeDAO().insertEntry(_entry);
                    } else {
                        //update entry
                        _entry.setId(vestigeEntryId);
                        _entry.setCreatedOn(originalCreatedDate);
                        entryDb.vestigeDAO().updateEntry(_entry);
                    }


                    finish();
                }
            });
        }
    }


    public void updateUIFromEntry(VestigeEntry entry){
        textDescription.setText(entry.getDescription());
        setRadioButtonStatus(entry.getStatus());
        if(entry.getCreatedOn() != null)
            originalCreatedDate = entry.getCreatedOn();

    }

public int getStatusFromRadioButton(){
        int status =  1;
        int radioButtonId = ((RadioGroup) findViewById(R.id.priority_radioGroup)).getCheckedRadioButtonId();
        switch (radioButtonId){
            case R.id.rdb_normal:
                status = STATUS_NORMAL;
                break;
            case R.id.rdb_important:
                status = STATUS_IMPORTANT;
                break;
            case R.id.rdb_urgent:
                status = STATUS_URGENT;
        }
    return status;
}

public void setRadioButtonStatus(int statusCode){

        switch (statusCode){
            case STATUS_NORMAL:
                ((RadioGroup) findViewById(R.id.priority_radioGroup)).check(R.id.rdb_normal);
                break;
            case STATUS_IMPORTANT:
                ((RadioGroup) findViewById(R.id.priority_radioGroup)).check(R.id.rdb_important);
                break;
            case STATUS_URGENT:
                ((RadioGroup) findViewById(R.id.priority_radioGroup)).check(R.id.rdb_urgent);
                break;
        }

}

}
